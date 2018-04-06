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

import org.ballerinax.kubernetes.exceptions.KubernetesPluginException;

/**
 * Annotation processor factory.
 */
public class AnnotationProcessorFactory {

    public static AnnotationProcessor getAnnotationProcessorInstance(String type) throws KubernetesPluginException {
        switch (type) {
            case "Service":
                return new ServiceAnnotationProcessor();
            case "Ingress":
                return new IngressAnnotationProcessor();
            case "HPA":
                return new HPAAnnotationProcessor();
            case "Deployment":
                return new DeploymentAnnotationProcessor();
            case "Secret":
                return new SecretAnnotationProcessor();
            case "ConfigMap":
                return new ConfigMapAnnotationProcessor();
            case "PersistentVolumeClaim":
                return new VolumeClaimAnnotationProcessor();
            case "Run":
                return new RunAnnotationProcessor();
            default:
                throw new KubernetesPluginException("Error while getting annotation processor for type: " + type);
        }
    }
}
