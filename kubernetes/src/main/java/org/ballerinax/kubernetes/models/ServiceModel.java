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

import org.ballerinax.kubernetes.KubernetesConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Kubernetes service annotations model class.
 */
public class ServiceModel extends KubernetesModel {
    private Map<String, String> labels;
    private String serviceType;
    private int port;
    private String selector;
    private boolean externalService;

    public ServiceModel() {
        serviceType = KubernetesConstants.ServiceType.ClusterIP.name();
        labels = new HashMap<>();
        setExternalService(false);
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    public void addLabel(String key, String value) {
        this.labels.put(key, value);
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getSelector() {
        return selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    @Override
    public String toString() {
        return "ServiceModel{" +
                "name='" + getName() + '\'' +
                ", labels=" + labels +
                ", serviceType='" + serviceType + '\'' +
                ", port=" + port +
                ", selector='" + selector + '\'' +
                '}';
    }

    public boolean isExternalService() {
        return externalService;
    }

    public void setExternalService(boolean externalService) {
        this.externalService = externalService;
    }
}
