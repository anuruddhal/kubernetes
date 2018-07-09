/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.ballerinax.kubernetes.processors;

import org.ballerinalang.model.tree.AnnotationAttachmentNode;
import org.ballerinalang.model.tree.EndpointNode;
import org.ballerinalang.model.tree.ServiceNode;
import org.ballerinax.kubernetes.KubernetesConstants;
import org.ballerinax.kubernetes.exceptions.KubernetesPluginException;
import org.ballerinax.kubernetes.models.DeploymentModel;
import org.ballerinax.kubernetes.models.ExternalFileModel;
import org.ballerinax.kubernetes.models.KubernetesContext;
import org.wso2.ballerinalang.compiler.tree.BLangAnnotationAttachment;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangArrayLiteral;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangExpression;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangRecordLiteral;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.ballerinax.kubernetes.KubernetesConstants.CONTAINER;
import static org.ballerinax.kubernetes.KubernetesConstants.DEPLOYMENT_POSTFIX;
import static org.ballerinax.kubernetes.KubernetesConstants.DOCKER_CERT_PATH;
import static org.ballerinax.kubernetes.KubernetesConstants.DOCKER_HOST;
import static org.ballerinax.kubernetes.utils.KubernetesUtils.getMap;
import static org.ballerinax.kubernetes.utils.KubernetesUtils.getValidName;
import static org.ballerinax.kubernetes.utils.KubernetesUtils.isBlank;
import static org.ballerinax.kubernetes.utils.KubernetesUtils.resolveValue;

/**
 * Deployment Annotation processor.
 */
public class DeploymentAnnotationProcessor extends AbstractAnnotationProcessor {

    @Override
    public void processAnnotation(ServiceNode entityName, AnnotationAttachmentNode attachmentNode) throws
            KubernetesPluginException {
        KubernetesContext.getInstance().getDataHolder().setDeploymentModel(processDeployment(attachmentNode));
    }

    @Override
    public void processAnnotation(EndpointNode endpointNode, AnnotationAttachmentNode attachmentNode)
            throws KubernetesPluginException {
        String endpointType = endpointNode.getEndPointType().getTypeName().getValue();
        if (endpointType.equals(CONTAINER)) {
            DeploymentModel deploymentModel = processExternalDeployment(endpointNode, attachmentNode);
            KubernetesContext.getInstance().getDataHolder().addEndpointToDeploymentMap(endpointNode, deploymentModel);
        } else {
            DeploymentModel deploymentModel = processDeployment(attachmentNode);
            KubernetesContext.getInstance().getDataHolder().setDeploymentModel(deploymentModel);
        }
    }

    private DeploymentModel processDeployment(AnnotationAttachmentNode attachmentNode)
            throws KubernetesPluginException {
        DeploymentModel deploymentModel = KubernetesContext.getInstance().getDataHolder().getDeploymentModel();
        List<BLangRecordLiteral.BLangRecordKeyValue> keyValues =
                ((BLangRecordLiteral) ((BLangAnnotationAttachment) attachmentNode).expr).getKeyValuePairs();
        for (BLangRecordLiteral.BLangRecordKeyValue keyValue : keyValues) {
            DeploymentConfiguration deploymentConfiguration =
                    DeploymentConfiguration.valueOf(keyValue.getKey().toString());
            String annotationValue = resolveValue(keyValue.getValue().toString());
            switch (deploymentConfiguration) {
                case name:
                    deploymentModel.setName(getValidName(annotationValue));
                    break;
                case labels:
                    deploymentModel.setLabels(getMap(((BLangRecordLiteral) keyValue.valueExpr).keyValuePairs));
                    break;
                case enableLiveness:
                    deploymentModel.setEnableLiveness(Boolean.valueOf(annotationValue));
                    break;
                case livenessPort:
                    deploymentModel.setLivenessPort(Integer.parseInt(annotationValue));
                    break;
                case initialDelaySeconds:
                    deploymentModel.setInitialDelaySeconds(Integer.parseInt(annotationValue));
                    break;
                case periodSeconds:
                    deploymentModel.setPeriodSeconds(Integer.parseInt(annotationValue));
                    break;
                case username:
                    deploymentModel.setUsername(annotationValue);
                    break;
                case env:
                    deploymentModel.setEnv(getMap(((BLangRecordLiteral) keyValue.valueExpr).keyValuePairs));
                    break;
                case password:
                    deploymentModel.setPassword(annotationValue);
                    break;
                case baseImage:
                    deploymentModel.setBaseImage(annotationValue);
                    break;
                case push:
                    deploymentModel.setPush(Boolean.valueOf(annotationValue));
                    break;
                case buildImage:
                    deploymentModel.setBuildImage(Boolean.valueOf(annotationValue));
                    break;
                case image:
                    deploymentModel.setImage(annotationValue);
                    break;
                case dockerHost:
                    deploymentModel.setDockerHost(annotationValue);
                    break;
                case dockerCertPath:
                    deploymentModel.setDockerCertPath(annotationValue);
                    break;
                case imagePullPolicy:
                    deploymentModel.setImagePullPolicy(annotationValue);
                    break;
                case imagePullSecret:
                    deploymentModel.setImagePullSecret(annotationValue);
                    break;
                case replicas:
                    deploymentModel.setReplicas(Integer.parseInt(annotationValue));
                    break;
                case copyFiles:
                    deploymentModel.setExternalFiles(getExternalFileMap(keyValue));
                    break;
                case singleYAML:
                    deploymentModel.setSingleYAML(Boolean.valueOf(annotationValue));
                    break;
                case dependsOn:
                    deploymentModel.setDependsOn(getDependsOn(keyValue));
                    break;
                default:
                    break;
            }
        }

        String dockerHost = System.getenv(DOCKER_HOST);
        if (!isBlank(dockerHost)) {
            deploymentModel.setDockerHost(dockerHost);
        }
        String dockerCertPath = System.getenv(DOCKER_CERT_PATH);
        if (!isBlank(dockerCertPath)) {
            deploymentModel.setDockerCertPath(dockerCertPath);
        }
        return deploymentModel;
    }

    private DeploymentModel processExternalDeployment(EndpointNode endpointNode, AnnotationAttachmentNode
            attachmentNode)
            throws KubernetesPluginException {
        DeploymentModel deploymentModel = KubernetesContext.getInstance().getDataHolder().getDeployment(endpointNode
                .getName().getValue());
        List<BLangRecordLiteral.BLangRecordKeyValue> keyValues =
                ((BLangRecordLiteral) ((BLangAnnotationAttachment) attachmentNode).expr).getKeyValuePairs();
        for (BLangRecordLiteral.BLangRecordKeyValue keyValue : keyValues) {
            DeploymentConfiguration deploymentConfiguration =
                    DeploymentConfiguration.valueOf(keyValue.getKey().toString());
            String annotationValue = resolveValue(keyValue.getValue().toString());
            switch (deploymentConfiguration) {
                //TODO:Validate all attributes
                case name:
                    deploymentModel.setName(getValidName(annotationValue));
                    break;
                case labels:
                    deploymentModel.setLabels(getMap(((BLangRecordLiteral) keyValue.valueExpr).keyValuePairs));
                    break;
                case enableLiveness:
                    deploymentModel.setEnableLiveness(Boolean.valueOf(annotationValue));
                    break;
                case livenessPort:
                    deploymentModel.setLivenessPort(Integer.parseInt(annotationValue));
                    break;
                case initialDelaySeconds:
                    deploymentModel.setInitialDelaySeconds(Integer.parseInt(annotationValue));
                    break;
                case periodSeconds:
                    deploymentModel.setPeriodSeconds(Integer.parseInt(annotationValue));
                    break;
                case env:
                    deploymentModel.setEnv(getMap(((BLangRecordLiteral) keyValue.valueExpr).keyValuePairs));
                    break;
                case image:
                    throw new KubernetesPluginException("Attribute image is not supported for docker:Container " +
                            "endpoint deployment");
                case imagePullPolicy:
                    deploymentModel.setImagePullPolicy(annotationValue);
                    break;
                case imagePullSecret:
                    deploymentModel.setImagePullSecret(annotationValue);
                    break;
                case replicas:
                    deploymentModel.setReplicas(Integer.parseInt(annotationValue));
                    break;
                case copyFiles:
                    deploymentModel.setExternalFiles(getExternalFileMap(keyValue));
                    break;
                case singleYAML:
                    deploymentModel.setSingleYAML(Boolean.valueOf(annotationValue));
                    break;
                default:
                    break;
            }
        }
        if (isBlank(deploymentModel.getName())) {
            deploymentModel.setName(getValidName(endpointNode.getName().getValue()) + DEPLOYMENT_POSTFIX);
        }
        deploymentModel.addLabel(KubernetesConstants.KUBERNETES_SELECTOR_KEY, getValidName(endpointNode.getName()
                .getValue()));
        if (deploymentModel.isEnableLiveness() && deploymentModel.getLivenessPort() == 0) {
            throw new KubernetesPluginException("@kubernetes:Deployment{} livenessPort cannot be empty.");
        }
        deploymentModel.setBuildImage(false);
        deploymentModel.setPush(false);
        return deploymentModel;
    }

    private Set<ExternalFileModel> getExternalFileMap(BLangRecordLiteral.BLangRecordKeyValue keyValue) throws
            KubernetesPluginException {
        Set<ExternalFileModel> externalFiles = new HashSet<>();
        List<BLangExpression> configAnnotation = ((BLangArrayLiteral) keyValue.valueExpr).exprs;
        for (BLangExpression bLangExpression : configAnnotation) {
            List<BLangRecordLiteral.BLangRecordKeyValue> annotationValues =
                    ((BLangRecordLiteral) bLangExpression).getKeyValuePairs();
            ExternalFileModel externalFileModel = new ExternalFileModel();
            for (BLangRecordLiteral.BLangRecordKeyValue annotation : annotationValues) {
                String annotationValue = resolveValue(annotation.getValue().toString());
                switch (annotation.getKey().toString()) {
                    case "source":
                        externalFileModel.setSource(annotationValue);
                        break;
                    case "target":
                        externalFileModel.setTarget(annotationValue);
                        break;
                    default:
                        break;
                }
            }
            if (isBlank(externalFileModel.getSource())) {
                throw new KubernetesPluginException("@kubernetes:Deployment copyFiles source cannot be empty.");
            }
            if (isBlank(externalFileModel.getTarget())) {
                throw new KubernetesPluginException("@kubernetes:Deployment copyFiles target cannot be empty.");
            }
            externalFiles.add(externalFileModel);
        }
        return externalFiles;
    }

    private Set<String> getDependsOn(BLangRecordLiteral.BLangRecordKeyValue keyValue) throws KubernetesPluginException {
        Set<String> dependsOnList = new HashSet<>();
        List<BLangExpression> configAnnotation = ((BLangArrayLiteral) keyValue.valueExpr).exprs;
        for (BLangExpression bLangExpression : configAnnotation) {
            dependsOnList.add(bLangExpression.toString());
        }
        return dependsOnList;
    }

    /**
     * Enum class for DeploymentConfiguration.
     */
    private enum DeploymentConfiguration {
        name,
        labels,
        replicas,
        enableLiveness,
        livenessPort,
        initialDelaySeconds,
        periodSeconds,
        imagePullPolicy,
        imagePullSecret,
        image,
        env,
        buildImage,
        dockerHost,
        username,
        password,
        baseImage,
        push,
        dockerCertPath,
        copyFiles,
        singleYAML,
        dependsOn
    }
}
