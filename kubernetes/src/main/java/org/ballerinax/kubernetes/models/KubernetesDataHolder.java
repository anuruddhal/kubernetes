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

package org.ballerinax.kubernetes.models;

import org.ballerinalang.model.tree.EndpointNode;
import org.ballerinax.kubernetes.KubernetesConstants;
import org.ballerinax.kubernetes.exceptions.KubernetesPluginException;
import org.wso2.ballerinalang.compiler.tree.BLangEndpoint;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangRecordLiteral;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.ballerinax.kubernetes.utils.KubernetesUtils.getMap;
import static org.ballerinax.kubernetes.utils.KubernetesUtils.getValidName;
import static org.ballerinax.kubernetes.utils.KubernetesUtils.isBlank;

/**
 * Class to store kubernetes models.
 */
public class KubernetesDataHolder {
    private boolean canProcess;
    private DeploymentModel deploymentModel;
    private DockerModel dockerModel;
    private Map<String, ServiceModel> bEndpointToK8sServiceMap;
    private Map<String, Set<SecretModel>> endpointToSecretMap;
    private Map<String, CompositeContainerModel> endpointToContainerModelMap;
    private Set<IngressModel> ingressModelSet;
    private JobModel jobModel;
    private String balxFilePath;
    private String outputDir;
    private Map<String, DeploymentModel> endpointToDeploymentMap;

    KubernetesDataHolder() {
        this.bEndpointToK8sServiceMap = new HashMap<>();
        this.endpointToSecretMap = new HashMap<>();
        this.ingressModelSet = new HashSet<>();
        this.endpointToContainerModelMap = new HashMap<>();
        this.deploymentModel = new DeploymentModel();
        this.endpointToDeploymentMap = new HashMap<>();
    }

    public DeploymentModel getDeploymentModel() {
        return deploymentModel;
    }

    public void setDeploymentModel(DeploymentModel deploymentModel) {
        this.deploymentModel = deploymentModel;
    }

    public Map<String, Set<SecretModel>> getSecretModels() {
        return endpointToSecretMap;
    }

    public void addEndpointSecret(String endpointName, Set<SecretModel> secretModel) {
        this.endpointToSecretMap.put(endpointName, secretModel);
    }

    public Map<String, ServiceModel> getbEndpointToK8sServiceMap() {
        return bEndpointToK8sServiceMap;
    }

    public void addBEndpointToK8sServiceMap(String endpointName, ServiceModel serviceModel) {
        this.bEndpointToK8sServiceMap.put(endpointName, serviceModel);
    }

    public ServiceModel getServiceModel(String endpointName) {
        return bEndpointToK8sServiceMap.get(endpointName);
    }

    public Set<IngressModel> getIngressModelSet() {
        return ingressModelSet;
    }

    public void addIngressModel(IngressModel ingressModel) {
        this.ingressModelSet.add(ingressModel);
    }

    public JobModel getJobModel() {
        return jobModel;
    }

    public void setJobModel(JobModel jobModel) {
        this.jobModel = jobModel;
    }

    public boolean isCanProcess() {
        return canProcess;
    }

    public void setCanProcess(boolean canProcess) {
        this.canProcess = canProcess;
    }

    public String getBalxFilePath() {
        return balxFilePath;
    }

    public void setBalxFilePath(String balxFilePath) {
        this.balxFilePath = balxFilePath;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public DockerModel getDockerModel() {
        return dockerModel;
    }

    public void setDockerModel(DockerModel dockerModel) {
        this.dockerModel = dockerModel;
    }

    public Map<String, CompositeContainerModel> getEndpointToContainerModelMap() {
        return this.endpointToContainerModelMap;
    }

    public Map<String, DeploymentModel> getEndpointToDeploymentMap() {
        return endpointToDeploymentMap;
    }

    public DeploymentModel getDeployment(String endpointName) {
        return endpointToDeploymentMap.get(endpointName);
    }

    public void addEndpointToDeploymentMap(EndpointNode endpointNode, DeploymentModel deploymentModel) throws
            KubernetesPluginException {
        if (deploymentModel == null) {
            deploymentModel = getExternalDeployment(endpointNode);
        }
        this.endpointToDeploymentMap.put(endpointNode.getName().getValue(), deploymentModel);
    }

    private DeploymentModel getExternalDeployment(EndpointNode endpointNode) throws KubernetesPluginException {
        DeploymentModel deploymentModel = new DeploymentModel();
        List<BLangRecordLiteral.BLangRecordKeyValue> endpointConfig =
                ((BLangRecordLiteral) ((BLangEndpoint) endpointNode).configurationExpr).getKeyValuePairs();
        for (BLangRecordLiteral.BLangRecordKeyValue keyValue : endpointConfig) {
            String key = keyValue.getKey().toString();
            if ("port".equals(key)) {
                try {
                    deploymentModel.addPort(Integer.parseInt(keyValue.getValue().toString()));
                } catch (NumberFormatException e) {
                    throw new KubernetesPluginException("Endpoint port must be an integer to use " +
                            "@kubernetes annotations.");
                }
            } else if ("image".equals(key)) {
                deploymentModel.setImage(keyValue.getValue().toString());
            } else if ("env".equals(key)) {
                deploymentModel.addEnv(getMap(((BLangRecordLiteral) keyValue.valueExpr).keyValuePairs));
            }
        }
        if (isBlank(deploymentModel.getImage())) {
            throw new KubernetesPluginException("image is not defined in the endpoint " + endpointNode
                    .getName().getValue());
        }
        deploymentModel.setName(getValidName(endpointNode.getName().getValue()));
        deploymentModel.addLabel(KubernetesConstants.KUBERNETES_SELECTOR_KEY, getValidName(endpointNode.getName()
                .getValue()));
        return deploymentModel;
    }
}
