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
import org.ballerinax.kubernetes.exceptions.KubernetesPluginException;
import org.ballerinax.kubernetes.models.KubernetesContext;
import org.ballerinax.kubernetes.models.PersistentVolumeClaimModel;
import org.wso2.ballerinalang.compiler.tree.BLangAnnotationAttachment;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangArrayLiteral;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangExpression;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangRecordLiteral;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.ballerinax.kubernetes.KubernetesConstants.CONTAINER;
import static org.ballerinax.kubernetes.utils.KubernetesUtils.getValidName;
import static org.ballerinax.kubernetes.utils.KubernetesUtils.isBlank;
import static org.ballerinax.kubernetes.utils.KubernetesUtils.resolveValue;

/**
 * Persistent volume claim annotation processor.
 */
public class VolumeClaimAnnotationProcessor extends AbstractAnnotationProcessor {


    @Override
    public void processAnnotation(EndpointNode endpointNode, AnnotationAttachmentNode attachmentNode) throws
            KubernetesPluginException {
        String endpointType = endpointNode.getEndPointType().getTypeName().getValue();
        if (!endpointType.equals(CONTAINER)) {
            throw new KubernetesPluginException("@kubernetes:VolumeClaim{} annotation is only allowed in container " +
                    "endpoints or services. Found " + endpointType);
        }
        KubernetesContext.getInstance().getDataHolder().getDeployment(endpointNode.getName().getValue())
                .setVolumeClaimModels(processVolumeClaimAnnotation(attachmentNode));
    }

    @Override
    public void processAnnotation(ServiceNode serviceNode, AnnotationAttachmentNode attachmentNode) throws
            KubernetesPluginException {
        KubernetesContext.getInstance().getDataHolder().getDeploymentModel().addPersistentVolumeClaims
                (processVolumeClaimAnnotation(attachmentNode));
    }

    private Set<PersistentVolumeClaimModel> processVolumeClaimAnnotation(AnnotationAttachmentNode attachmentNode)
            throws KubernetesPluginException {
        Set<PersistentVolumeClaimModel> volumeClaimModels = new HashSet<>();
        List<BLangRecordLiteral.BLangRecordKeyValue> keyValues =
                ((BLangRecordLiteral) ((BLangAnnotationAttachment) attachmentNode).expr).getKeyValuePairs();
        for (BLangRecordLiteral.BLangRecordKeyValue keyValue : keyValues) {
            List<BLangExpression> secretAnnotation = ((BLangArrayLiteral) keyValue.valueExpr).exprs;
            for (BLangExpression bLangExpression : secretAnnotation) {
                PersistentVolumeClaimModel claimModel = new PersistentVolumeClaimModel();
                List<BLangRecordLiteral.BLangRecordKeyValue> annotationValues =
                        ((BLangRecordLiteral) bLangExpression).getKeyValuePairs();
                for (BLangRecordLiteral.BLangRecordKeyValue annotation : annotationValues) {
                    VolumeClaimConfig volumeMountConfig =
                            VolumeClaimConfig.valueOf(annotation.getKey().toString());
                    String annotationValue = resolveValue(annotation.getValue().toString());
                    switch (volumeMountConfig) {
                        case name:
                            claimModel.setName(getValidName(annotationValue));
                            break;
                        case mountPath:
                            claimModel.setMountPath(annotationValue);
                            break;
                        case accessMode:
                            claimModel.setAccessMode(annotationValue);
                            break;
                        case volumeClaimSize:
                            claimModel.setVolumeClaimSize(annotationValue);
                            break;
                        case readOnly:
                            claimModel.setReadOnly(Boolean.parseBoolean(annotationValue));
                            break;
                        default:
                            break;
                    }
                }
                if (isBlank(claimModel.getName())) {
                    throw new KubernetesPluginException("Volume claim name cannot be empty.");
                }
                if (isBlank(claimModel.getMountPath())) {
                    throw new KubernetesPluginException("Volume claim mount path cannot be empty.");
                }
                if (isBlank(claimModel.getVolumeClaimSize())) {
                    throw new KubernetesPluginException("Volume claim size cannot be empty.");
                }
                volumeClaimModels.add(claimModel);
            }
        }
        return volumeClaimModels;
    }

    /**
     * Enum class for volume configurations.
     */
    private enum VolumeClaimConfig {
        name,
        mountPath,
        readOnly,
        accessMode,
        volumeClaimSize
    }
}
