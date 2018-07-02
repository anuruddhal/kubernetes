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

package org.ballerinax.kubernetes.handlers;

import org.ballerinax.kubernetes.exceptions.KubernetesPluginException;
import org.ballerinax.kubernetes.models.CompositeContainerModel;
import org.ballerinax.kubernetes.utils.KubernetesUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * Generates Composite YAML files.
 */
public class CompositeContainerHandler extends AbstractArtifactHandler {


    @Override
    public void createArtifacts() throws KubernetesPluginException {
        Iterator<Map.Entry<String, CompositeContainerModel>> iterator = dataHolder.getEndpointToContainerModelMap()
                .entrySet().iterator();
        if (iterator.hasNext()) {
            OUT.println();
        }
        int count = 0;
        while (iterator.hasNext()) {
            count++;
            Map.Entry<String, CompositeContainerModel> pair = iterator.next();
            try {
                String deploymentContent = new DeploymentHandler().generate(pair.getValue().getDeploymentModel());
                String svcContent = new ServiceHandler().generate(pair.getValue().getServiceModel());
                KubernetesUtils.writeToFileComposite(deploymentContent);
                KubernetesUtils.writeToFileComposite(svcContent);
            } catch (IOException e) {
                String errorMessage = "Error while generating yaml file for composite deployment.";
                throw new KubernetesPluginException(errorMessage, e);
            }
            OUT.print("\t@composite:Container \t\t\t - complete " + count + "/" + dataHolder
                    .getEndpointToContainerModelMap().size() + "\r");
        }


    }

}
