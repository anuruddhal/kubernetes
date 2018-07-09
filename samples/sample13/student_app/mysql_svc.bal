import ballerinax/docker;
import ballerina/io;
import ballerinax/kubernetes;

@kubernetes:Service {
    serviceType: "NodePort"
}
@kubernetes:Deployment {
}
@kubernetes:Secret {
    secrets: [
        { name: "private", mountPath: "/tmp/mysecret",
            data: ["./secrets/MySecret1.txt"]
        }
    ]
}
endpoint docker:Container mysql_ep {
    port: 3306,
    host: "mysql-svc",
    image: "ballerina_mysql:1.0.0",
    env: { "MYSQL_ROOT_PASSWORD": "root" }
};