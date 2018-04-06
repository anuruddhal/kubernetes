package org.ballerinax.kubernetes.processors;

import org.ballerinalang.model.tree.AnnotationAttachmentNode;
import org.ballerinalang.model.tree.EndpointNode;
import org.ballerinalang.model.tree.ServiceNode;
import org.ballerinax.kubernetes.exceptions.KubernetesPluginException;
import org.ballerinax.kubernetes.models.DeploymentModel;
import org.ballerinax.kubernetes.models.KubernetesDataHolder;
import org.wso2.ballerinalang.compiler.tree.BLangAnnotationAttachment;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangRecordLiteral;

import java.util.List;

import static org.ballerinax.kubernetes.utils.KubernetesUtils.getMap;
import static org.ballerinax.kubernetes.utils.KubernetesUtils.getValidName;
import static org.ballerinax.kubernetes.utils.KubernetesUtils.resolveValue;

/**
 * Deployment Annotation processor.
 */
public class DeploymentAnnotationProcessor implements AnnotationProcessor {

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
        image,
        env,
        buildImage,
        dockerHost,
        username,
        password,
        baseImage,
        push,
        dockerCertPath
    }

    @Override
    public void processAnnotation(ServiceNode entityName, AnnotationAttachmentNode attachmentNode) throws
            KubernetesPluginException {
        processDeployment(attachmentNode);
    }

    @Override
    public void processAnnotation(EndpointNode endpointNode, AnnotationAttachmentNode attachmentNode)
            throws KubernetesPluginException {
        processDeployment(attachmentNode);
    }

    private void processDeployment(AnnotationAttachmentNode attachmentNode) throws KubernetesPluginException {
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
                    deploymentModel.setEnableLiveness(annotationValue);
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
                case replicas:
                    deploymentModel.setReplicas(Integer.parseInt(annotationValue));
                    break;
                default:
                    break;
            }
        }
        KubernetesDataHolder.getInstance().setDeploymentModel(deploymentModel);
    }
}
