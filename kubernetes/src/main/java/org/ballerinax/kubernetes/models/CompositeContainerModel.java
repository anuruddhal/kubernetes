package org.ballerinax.kubernetes.models;

/**
 * Class to hold deployment and service info for composite container.
 */
public class CompositeContainerModel {
    private DeploymentModel deploymentModel;
    private ServiceModel serviceModel;

    public DeploymentModel getDeploymentModel() {
        return deploymentModel;
    }

    public void setDeploymentModel(DeploymentModel deploymentModel) {
        this.deploymentModel = deploymentModel;
    }

    public ServiceModel getServiceModel() {
        return serviceModel;
    }

    public void setServiceModel(ServiceModel serviceModel) {
        this.serviceModel = serviceModel;
    }
}
