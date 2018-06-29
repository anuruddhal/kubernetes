// Copyright (c) 2018 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.


documentation {Composite application configuration
    F{{name}} - Name of the deployment
    F{{labels}} - Map of labels for deployment
    F{{replicas}} - Number of replicas
    F{{enableLiveness}} - Enable/Disable liveness probe
    F{{livenessPort}} - Port to check the liveness
    F{{initialDelaySeconds}} - Initial delay in seconds before performing the first probe
    F{{periodSeconds}} - Liveness probe interval
    F{{imagePullPolicy}} - Kubernetes image pull policy
    F{{imagePullSecret}} - Kubernetes image pull secret
    F{{image}} - Docker image with tag
    F{{env}} - Environment varialbe map for containers
}
public type ContainerConfigurations record {
    string name;
    map labels;
    int replicas;
    boolean enableLiveness;
    int livenessPort;
    int initialDelaySeconds;
    int periodSeconds;
    string imagePullPolicy;
    string imagePullSecret;
    string image;
    map env;
};

documentation {@composite:ContainerConfig annotation to configure composite applications
}
public annotation<service> ContainerConfig ContainerConfigurations;
