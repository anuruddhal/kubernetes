import ballerinax/docker;
import ballerina/io;
import ballerinax/kubernetes;

@kubernetes:Service{
}
@kubernetes:Deployment{
    name:"mysql-deployment"
}
endpoint docker:Container mysql_ep {
    port: 3306,
    host: "mysql-svc",
    image: "ballerina_mysql:1.0.0",
    env: { "MYSQL_ROOT_PASSWORD": "root" }
};