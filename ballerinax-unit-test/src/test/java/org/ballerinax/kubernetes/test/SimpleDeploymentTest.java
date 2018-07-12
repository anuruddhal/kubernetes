/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.ballerinax.kubernetes.test;

import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinax.kubernetes.KubernetesPlugin;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Configuration auth provider testcase.
 */
public class SimpleDeploymentTest {
    private CompileResult compileResult;

    @BeforeClass
    public void setup() throws Exception {
        compileResult = BCompileUtil.compile("/Users/anuruddha/workspace/ballerinax/kubernetes/samples/sample1" +
                "/hello_world_k8s.bal");
    }


    @Test(description = "Test case for creating file based userstore")
    public void testCreateConfigAuthProvider() {
        Assert.assertEquals(0, compileResult.getErrorCount());
    }

}
