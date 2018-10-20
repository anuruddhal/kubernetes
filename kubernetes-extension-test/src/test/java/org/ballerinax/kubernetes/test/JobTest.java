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

package org.ballerinax.kubernetes.test;

import io.fabric8.docker.api.model.ImageInspect;
import io.fabric8.kubernetes.api.KubernetesHelper;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.Job;
import org.ballerinax.kubernetes.KubernetesConstants;
import org.ballerinax.kubernetes.exceptions.KubernetesPluginException;
import org.ballerinax.kubernetes.test.utils.KubernetesTestUtils;
import org.ballerinax.kubernetes.utils.KubernetesUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static org.ballerinax.kubernetes.KubernetesConstants.DOCKER;
import static org.ballerinax.kubernetes.KubernetesConstants.KUBERNETES;
import static org.ballerinax.kubernetes.test.utils.KubernetesTestUtils.getDockerImage;

public class JobTest {
    private final String sourceDirPath = Paths.get("src").resolve("test").resolve("resources").resolve("job")
            .toAbsolutePath().toString();
    private final String targetPath = sourceDirPath + File.separator + KUBERNETES;
    private final String dockerImage = "my-ballerina-job:1.0";


    @Test
    public void testKubernetesJobGeneration() throws IOException, InterruptedException {
        Assert.assertEquals(KubernetesTestUtils.compileBallerinaFile(sourceDirPath, "ballerina_job.bal"), 0);
        File dockerFile = new File(targetPath + File.separator + DOCKER + File.separator + "Dockerfile");
        Assert.assertTrue(dockerFile.exists());
        ImageInspect imageInspect = getDockerImage(dockerImage);
        Assert.assertNotNull(imageInspect.getContainerConfig());
        File jobYAML = new File(targetPath + File.separator + "ballerina_job_job.yaml");
        Job job = KubernetesHelper.loadYaml(jobYAML);
        Assert.assertEquals("ballerina-job-job", job.getMetadata().getName());
        Assert.assertEquals(job.getMetadata().getLabels().size(), 2, "Unmatching number of labels found.");
        Assert.assertEquals(job.getMetadata().getLabels().get("lang"), "ballerina", "Invalid label found.");

        Assert.assertEquals(job.getMetadata().getAnnotations().size(), 1, "Unmatching number of annotations found.");
        Assert.assertEquals(job.getMetadata().getAnnotations().get("log"), "info", "Invalid annotations found.");

        Assert.assertEquals(1, job.getSpec().getTemplate().getSpec().getContainers().size());
        Container container = job.getSpec().getTemplate().getSpec().getContainers().get(0);
        Assert.assertEquals(dockerImage, container.getImage());
        Assert.assertEquals(KubernetesConstants.ImagePullPolicy.IfNotPresent.name(), container.getImagePullPolicy());
        Assert.assertEquals(KubernetesConstants.RestartPolicy.Never.name(), job.getSpec().getTemplate().getSpec()
                .getRestartPolicy());
    }


    @AfterClass
    public void cleanUp() throws KubernetesPluginException {
        KubernetesUtils.deleteDirectory(targetPath);
        KubernetesTestUtils.deleteDockerImage(dockerImage);
    }
}
