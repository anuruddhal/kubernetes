import ballerinax/composite;
import ballerina/io;

endpoint composite:Listener mysql_ep {
    port: 3306,
    host: "mysql-svc"
};

@composite:ContainerConfig {
    name: "mysql-endpoint",
    labels: { "region": "SL" },
    replicas: 1,
    enableLiveness: true,
    livenessPort: 3306,
    image: "ballerina_mysql:1.0.0",
    env: { "MYSQL_ROOT_PASSWORD": "root" }
}
service<composite:Container> MySQL_Service bind mysql_ep {}
