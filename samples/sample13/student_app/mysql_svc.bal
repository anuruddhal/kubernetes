import ballerinax/docker;
import ballerina/io;
import ballerinax/kubernetes;

@kubernetes:Service {
    serviceType: "NodePort"
}
@kubernetes:Deployment {
}
@kubernetes:PersistentVolumeClaim {
    volumeClaims: [
        { name: "local-pv-2", mountPath: "/tmp/pvc", readOnly: false, accessMode: "ReadWriteOnce", volumeClaimSize
        : "1Gi" }
    ]
}
endpoint docker:Container mysql_ep {
    port: 3306,
    host: "mysql-svc",
    image: "ballerina_mysql:1.0.0",
    env: { "MYSQL_ROOT_PASSWORD": "root" }
};