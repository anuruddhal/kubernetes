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
import org.ballerinax.kubernetes.models.KubeConfigModel;
import org.ballerinax.kubernetes.models.KubernetesDataHolder;
import org.wso2.ballerinalang.compiler.tree.BLangAnnotationAttachment;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangRecordLiteral;

import java.util.List;

import static org.ballerinax.kubernetes.utils.KubernetesUtils.resolveValue;

public class RunAnnotationProcessor implements AnnotationProcessor {

    /**
     * Enum class for Run annotation configurations.
     */
    private enum KubernetesConfig {
        masterUrl,
        namespace,
        certFile,
        oauthTokenFile
    }

    @Override
    public void processAnnotation(ServiceNode serviceNode, AnnotationAttachmentNode attachmentNode) throws
            KubernetesPluginException {
        processRunAnnotation(attachmentNode);
    }

    @Override
    public void processAnnotation(EndpointNode endpointNode, AnnotationAttachmentNode attachmentNode) throws
            KubernetesPluginException {
        processRunAnnotation(attachmentNode);
    }

    private void processRunAnnotation(AnnotationAttachmentNode attachmentNode) throws KubernetesPluginException {
        KubeConfigModel kubeConfigModel = new KubeConfigModel();
        List<BLangRecordLiteral.BLangRecordKeyValue> keyValues =
                ((BLangRecordLiteral) ((BLangAnnotationAttachment) attachmentNode).expr).getKeyValuePairs();
        for (BLangRecordLiteral.BLangRecordKeyValue keyValue : keyValues) {
            KubernetesConfig kubernetesConfig =
                    KubernetesConfig.valueOf(keyValue.getKey().toString());
            String annotationValue = resolveValue(keyValue.getValue().toString());
            switch (kubernetesConfig) {
                case masterUrl:
                    kubeConfigModel.setMasterUrl(annotationValue);
                    break;
                case namespace:
                    kubeConfigModel.setNamespace(annotationValue);
                    break;
                case certFile:
                    kubeConfigModel.setCertFile(annotationValue);
                    break;
                case oauthTokenFile:
                    kubeConfigModel.setOauthTokenFile(annotationValue);
                    break;
                default:
                    break;
            }
        }
        KubernetesDataHolder.getInstance().setKubeConfigModel(kubeConfigModel);
    }
}
