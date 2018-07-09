import ballerina/io;
import ballerina/jdbc;
import ballerinax/docker;
import ballerina/http;
import ballerinax/kubernetes;

endpoint jdbc:Client testDB {
    url: "jdbc:mysql://" + docker:getHost(mysql_ep) + ":" + docker:getPort(mysql_ep) + "/testdb",
    username: "root",
    password: "root",
    poolOptions: { maximumPoolSize: 5 },
    dbOptions: { useSSL: false }
};

@kubernetes:Service {
    serviceType: "NodePort"
}
endpoint http:Listener student_ep {
    port: 9090
};

@kubernetes:HPA {}
@kubernetes:Deployment {
    singleYAML: false,
    copyFiles: [
        {
            target: "/ballerina/runtime/bre/lib",
            source: "./conf/mysql-connector-java-5.1.46.jar"
        }
    ],
    dependsOn: ["mysql-svc"]
}
@http:ServiceConfig {
    basePath: "/students"
}
service<http:Service> Student_APP bind student_ep {
    @http:ResourceConfig {
        methods: ["GET"],
        path: "/"
    }
    getStudent(endpoint outboundEP, http:Request request) {
        http:Response response = new;

        var selectRet = testDB->select("SELECT * FROM student", ());
        table dt;
        match selectRet {
            table tableReturned => dt = tableReturned;
            error e => io:println("Select data from student table failed: "
                    + e.message);
        }

        io:println("\nConvert the table into json");
        var jsonConversionRet = <json>dt;
        match jsonConversionRet {
            json jsonRes => {
                response.setJsonPayload(untaint jsonRes);
            }
            error e => {
                io:println("Error in table to json conversion");
                response.setTextPayload("Error in table to json conversion");
            }
        }
        _ = outboundEP->respond(response);

    }
}

