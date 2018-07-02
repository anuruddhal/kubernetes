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
import org.ballerinax.kubernetes.models.KubernetesContext;
import org.ballerinax.kubernetes.models.ServiceModel;
import org.wso2.ballerinalang.compiler.tree.BLangAnnotationAttachment;
import org.wso2.ballerinalang.compiler.tree.BLangEndpoint;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangRecordLiteral;

import java.util.List;

import static org.ballerinax.kubernetes.utils.KubernetesUtils.getMap;
import static org.ballerinax.kubernetes.utils.KubernetesUtils.getValidName;
import static org.ballerinax.kubernetes.utils.KubernetesUtils.isBlank;
import static org.ballerinax.kubernetes.utils.KubernetesUtils.resolveValue;

/**
 * Composite Config Annotation processor.
 */
public class ContainerConfigAnnotationProcessor extends AbstractAnnotationProcessor {

    @Override
    public void processAnnotation(ServiceNode serviceNode, AnnotationAttachmentNode attachmentNode) throws
            KubernetesPluginException {
        // Process Deployment Annotation
        DeploymentModel deploymentModel = new DeploymentModel();
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
                case env:
                    deploymentModel.setEnv(getMap(((BLangRecordLiteral) keyValue.valueExpr).keyValuePairs));
                    break;
                case image:
                    deploymentModel.setImage(annotationValue);
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
                default:
                    break;
            }
        }
        if (isBlank(deploymentModel.getName())) {
            throw new KubernetesPluginException("@composite:ContainerConfig{}: name attribute cannot be empty");
        }
        if (isBlank(deploymentModel.getImage())) {
            throw new KubernetesPluginException("@composite:ContainerConfig{}: image attribute cannot be empty");
        }
        deploymentModel.setBuildImage(false);
        //TODO: Verify and handle multiple bounds.
        String boundEndpoint = serviceNode.getBoundEndpoints().get(0).getVariableName().getValue();
        deploymentModel.addLabel(KubernetesConstants.KUBERNETES_SELECTOR_KEY, boundEndpoint);
        KubernetesContext.getInstance().getDataHolder().addCompositeDeploymentModel(boundEndpoint, deploymentModel);
    }

    @Override
    public void processAnnotation(EndpointNode endpointNode, AnnotationAttachmentNode attachmentNode) throws
            KubernetesPluginException {
        List<BLangRecordLiteral.BLangRecordKeyValue> keyValues =
                ((BLangRecordLiteral) ((BLangEndpoint) endpointNode).configurationExpr).getKeyValuePairs();
        ServiceModel serviceModel = new ServiceModel();
        for (BLangRecordLiteral.BLangRecordKeyValue keyValue : keyValues) {
            CompositeEndpointConfiguration endpointConfiguration = CompositeEndpointConfiguration.valueOf(keyValue
                    .getKey().toString());
            switch (endpointConfiguration) {
                case host:
                    //validate service hostname
                    String providedHost = keyValue.getValue().toString();
                    String validatedHost = getValidName(providedHost);
                    if (!providedHost.equals(validatedHost)) {
                        throw new KubernetesPluginException("Invalid hostname " + providedHost);
                    }
                    serviceModel.setName(validatedHost);
                    break;
                case port:
                    try {
                        int port = Integer.parseInt(keyValue.getValue().toString());
                        serviceModel.setPort(port);
                    } catch (NumberFormatException e) {
                        throw new KubernetesPluginException("Composite Listener endpoint port must be an integer. " +
                                "Found: " + keyValue.getValue().toString());
                    }
                    break;
                default:
                    break;
            }

        }
        String endpointName = endpointNode.getName().getValue();
        serviceModel.setSelector(endpointName);
        KubernetesContext.getInstance().getDataHolder().addCompositeServiceModel(endpointName, serviceModel);
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
        env
    }

    /**
     * Enum class for DeploymentConfiguration.
     */
    private enum CompositeEndpointConfiguration {
        host,
        port
    }
}
