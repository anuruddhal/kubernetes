/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinax.kubernetes.handlers;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.CronJob;
import io.fabric8.kubernetes.api.model.CronJobBuilder;
import io.fabric8.kubernetes.api.model.Job;
import io.fabric8.kubernetes.api.model.JobBuilder;
import io.fabric8.kubernetes.api.model.LocalObjectReference;
import io.fabric8.kubernetes.api.model.LocalObjectReferenceBuilder;
import io.fabric8.kubernetes.client.internal.SerializationUtils;
import org.ballerinax.kubernetes.KubernetesConstants;
import org.ballerinax.kubernetes.exceptions.KubernetesPluginException;
import org.ballerinax.kubernetes.models.DockerModel;
import org.ballerinax.kubernetes.models.JobModel;
import org.ballerinax.kubernetes.utils.KubernetesUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.ballerinax.kubernetes.KubernetesConstants.BALX;
import static org.ballerinax.kubernetes.KubernetesConstants.DOCKER_LATEST_TAG;
import static org.ballerinax.kubernetes.KubernetesConstants.JOB_FILE_POSTFIX;
import static org.ballerinax.kubernetes.KubernetesConstants.JOB_POSTFIX;
import static org.ballerinax.kubernetes.KubernetesConstants.YAML;
import static org.ballerinax.kubernetes.utils.KubernetesUtils.getValidName;
import static org.ballerinax.kubernetes.utils.KubernetesUtils.isBlank;
import static org.ballerinax.kubernetes.utils.KubernetesUtils.populateEnvVar;

/**
 * Job generator.
 */
public class JobHandler extends AbstractArtifactHandler {


    private void generate(JobModel jobModel) throws KubernetesPluginException {
        try {
            String jobContent;
            if (isBlank(jobModel.getSchedule())) {
                jobContent = SerializationUtils.dumpWithoutRuntimeStateAsYaml(getJob(jobModel));
            } else {
                jobContent = SerializationUtils.dumpWithoutRuntimeStateAsYaml(getCronJob(jobModel));
            }
            KubernetesUtils.writeToFile(jobContent, JOB_FILE_POSTFIX + YAML);
        } catch (IOException e) {
            String errorMessage = "Error while generating yaml file for job " + jobModel.getName();
            throw new KubernetesPluginException(errorMessage, e);
        }

    }


    private Container generateContainer(JobModel jobModel) {
        return new ContainerBuilder()
                .withName(jobModel.getName())
                .withImage(jobModel.getImage())
                .withImagePullPolicy(jobModel.getImagePullPolicy())
                .withEnv(populateEnvVar(jobModel.getEnv()))
                .build();
    }

    private Job getJob(JobModel jobModel) {
        JobBuilder jobBuilder = new JobBuilder()
                .withNewMetadata()
                .withName(jobModel.getName())
                .withAnnotations(jobModel.getAnnotations())
                .withLabels(jobModel.getLabels())
                .withNamespace(dataHolder.getNamespace())
                .endMetadata()
                .withNewSpec()
                .withNewTemplate()
                .withNewSpec()
                .withRestartPolicy(jobModel.getRestartPolicy())
                .withContainers(generateContainer(jobModel))
                .withImagePullSecrets(getImagePullSecrets(jobModel))
                .endSpec()
                .endTemplate()
                .endSpec();
        return jobBuilder.build();
    }

    private List<LocalObjectReference> getImagePullSecrets(JobModel jobModel) {
        List<LocalObjectReference> imagePullSecrets = new ArrayList<>();
        for (String imagePullSecret : jobModel.getImagePullSecrets()) {
            imagePullSecrets.add(new LocalObjectReferenceBuilder().withName(imagePullSecret).build());
        }
        return imagePullSecrets;
    }

    private CronJob getCronJob(JobModel jobModel) {
        return new CronJobBuilder()
                .withNewMetadata()
                .withName(jobModel.getName())
                .endMetadata()
                .withNewSpec()
                .withNewJobTemplate()
                .withNewSpec()
                .withActiveDeadlineSeconds((long) jobModel.getActiveDeadlineSeconds())
                .endSpec()
                .endJobTemplate()
                .withSchedule(jobModel.getSchedule())
                .endSpec()
                .build();
    }


    @Override
    public void createArtifacts() throws KubernetesPluginException {
        String balxFileName = KubernetesUtils.extractBalxName(dataHolder.getBalxFilePath());
        JobModel jobModel = dataHolder.getJobModel();
        if (isBlank(jobModel.getName())) {
            jobModel.setName(getValidName(balxFileName) + JOB_POSTFIX);
        }
        if (isBlank(jobModel.getImage())) {
            jobModel.setImage(balxFileName + DOCKER_LATEST_TAG);
        }
        jobModel.addLabel(KubernetesConstants.KUBERNETES_SELECTOR_KEY, balxFileName);
        generate(jobModel);
        //generate dockerfile and docker image
        dataHolder.setDockerModel(getDockerModel(jobModel));
        OUT.println();
        OUT.println("\t@kubernetes:Job \t\t\t - complete 1/1");
    }

    private DockerModel getDockerModel(JobModel jobModel) {
        DockerModel dockerModel = new DockerModel();
        String dockerImage = jobModel.getImage();
        String imageTag = dockerImage.substring(dockerImage.lastIndexOf(":") + 1, dockerImage.length());
        dockerModel.setBaseImage(jobModel.getBaseImage());
        dockerModel.setName(dockerImage);
        dockerModel.setTag(imageTag);
        dockerModel.setUsername(jobModel.getUsername());
        dockerModel.setPassword(jobModel.getPassword());
        dockerModel.setPush(jobModel.isPush());
        dockerModel.setBalxFileName(KubernetesUtils.extractBalxName(dataHolder.getBalxFilePath()) + BALX);
        dockerModel.setService(false);
        dockerModel.setDockerHost(jobModel.getDockerHost());
        dockerModel.setDockerCertPath(jobModel.getDockerCertPath());
        dockerModel.setBuildImage(jobModel.isBuildImage());
        dockerModel.setExternalFiles(jobModel.getExternalFiles());
        return dockerModel;
    }
}
