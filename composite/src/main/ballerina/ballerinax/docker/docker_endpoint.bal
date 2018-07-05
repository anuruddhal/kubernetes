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

/////////////////////////////
/// Docker Container Endpoint ///
/////////////////////////////
documentation {
    This is used for creating Docker Container endpoints. A Docker Container endpoint is capable of generating the artifacts for composite applications.
    The `Continer` is responsible for initializing the endpoint using the provided configurations and
    providing the actions for communicating with the caller.
}
public type Container object {

    private  ContainerConfig config;
    private  Remote conn;


    documentation {
        Gets invoked during package initialization to initialize the endpoint.

        P{{param_config}} - The CompositeEndpointConfiguration of the endpoint.
    }
    public function init(ContainerConfig containerConfig) {
        self.config = containerConfig;
        conn.host = containerConfig.host;
        conn.port = containerConfig.port;
    }


    documentation {
        Gets invoked when binding a service to the endpoint.

        P{{serviceType}} The type of the service to be registered
    }
    public function register(typedesc serviceType) {
    }

    documentation {
        Starts the registered service.
    }
    public function start() {
    }

    documentation {
        Returns the connector that client code uses.

        R{{}} The connector that client code uses
    }
    public function getCallerActions() returns (Remote) {
        return conn;
    }

    documentation {
        Stops the registered service.
    }
    public function stop() {
    }

    function getRemote() returns (Remote) {
        return conn;
    }
};

documentation {
    Presents a read-only view of the remote address.

    F{{host}} The remote host name/IP
    F{{port}} The remote port
}
public type Remote object {
        @readonly public string host;
        @readonly public int port;
};

documentation {
    Provides a set of configurations for Composite service endpoints.

    F{{host}} The hostname/IP of the endpoint
    F{{port}} The port to which the endpoint should bind to
    F{{image}} The docker image to pull
}
public type ContainerConfig record {
    string host,
    int port,
    string image,
    map env,
};

documentation {
    Returns a hostname/IP of the given composite endpoint

    P{{Container}} The Listner endpoint
    R{{}} The hostname of the endpoint
}
public function getHost(Container container) returns (string) {
    return container.getRemote().host;
}

documentation {
    Returns the port of the given composite endpoint

    P{{Container}} The Listner endpoint
    R{{}} The port of the composite endpoint
}
public function getPort(Container container) returns (int) {
    return container.getRemote().port;
}