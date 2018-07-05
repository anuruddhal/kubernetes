import ballerinax/docker;
import ballerina/io;
import ballerinax/kubernetes;
import kubernetes;

@kubernetes:Service{
    serviceType:"NodePort"
}
@kubernetes:Deployment{
    
}
endpoint docker:Container mysql_ep {
    port: 3306,
    host: "localhost",
    image: "ballerina_mysql:1.0.0",
    env: { "MYSQL_ROOT_PASSWORD": "root" }
};