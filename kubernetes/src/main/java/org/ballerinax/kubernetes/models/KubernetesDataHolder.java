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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class to store kubernetes models.
 */
public class KubernetesDataHolder {
    private boolean canProcess;
    private DeploymentModel deploymentModel;
    private DockerModel dockerModel;
    private PodAutoscalerModel podAutoscalerModel;
    private Map<String, ServiceModel> bEndpointToK8sServiceMap;
    private Map<String, Set<SecretModel>> endpointToSecretMap;
    private Map<String, CompositeContainerModel> endpointToContainerModelMap;
    private Set<SecretModel> secretModelSet;
    private Set<IngressModel> ingressModelSet;
    private Set<ConfigMapModel> configMapModelSet;
    private Set<PersistentVolumeClaimModel> volumeClaimModelSet;
    private JobModel jobModel;
    private String balxFilePath;
    private String outputDir;

    public KubernetesDataHolder() {
        this.bEndpointToK8sServiceMap = new HashMap<>();
        this.endpointToSecretMap = new HashMap<>();
        this.secretModelSet = new HashSet<>();
        this.configMapModelSet = new HashSet<>();
        this.volumeClaimModelSet = new HashSet<>();
        ingressModelSet = new HashSet<>();
        endpointToContainerModelMap = new HashMap<>();
    }

    public DeploymentModel getDeploymentModel() {
        return deploymentModel;
    }

    public void setDeploymentModel(DeploymentModel deploymentModel) {
        this.deploymentModel = deploymentModel;
    }

    public PodAutoscalerModel getPodAutoscalerModel() {
        return podAutoscalerModel;
    }

    public void setPodAutoscalerModel(PodAutoscalerModel podAutoscalerModel) {
        this.podAutoscalerModel = podAutoscalerModel;
    }

    public Map<String, Set<SecretModel>> getSecretModels() {
        return endpointToSecretMap;
    }

    public void addCompositeServiceModel(String endpointName, ServiceModel serviceModel) {
        //Add map with service model
        CompositeContainerModel containerModel = new CompositeContainerModel();
        containerModel.setServiceModel(serviceModel);
        this.endpointToContainerModelMap.put(endpointName, containerModel);
    }

    public void addCompositeDeploymentModel(String endpointName, DeploymentModel deploymentModel) {
        //Update the map with deployment model
        CompositeContainerModel containerModel = this.endpointToContainerModelMap.get(endpointName);
        deploymentModel.addPort(containerModel.getServiceModel().getPort());
        containerModel.setDeploymentModel(deploymentModel);
        this.endpointToContainerModelMap.put(endpointName, containerModel);
    }

    public void addEndpointSecret(String endpointName, Set<SecretModel> secretModel) {
        this.endpointToSecretMap.put(endpointName, secretModel);
    }

    public Set<SecretModel> getSecretModelSet() {
        return secretModelSet;
    }

    public void addSecrets(Set<SecretModel> secrets) {
        this.secretModelSet.addAll(secrets);
    }

    public Set<ConfigMapModel> getConfigMapModelSet() {
        return configMapModelSet;
    }

    public void addConfigMaps(Set<ConfigMapModel> configMaps) {
        this.configMapModelSet.addAll(configMaps);
    }

    public Set<PersistentVolumeClaimModel> getVolumeClaimModelSet() {
        return volumeClaimModelSet;
    }

    public void addPersistentVolumeClaims(Set<PersistentVolumeClaimModel> persistentVolumeClaims) {
        this.volumeClaimModelSet.addAll(persistentVolumeClaims);
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
}
