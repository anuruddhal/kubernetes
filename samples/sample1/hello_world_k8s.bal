import ballerina/io;
import ballerina/mysql;
import ballerinax/composite;
import ballerina/http;

//endpoint mysql:Client testDB {
//    host: composite:getHost(helloEP),
//    port: composite:getPort(helloEP),
//    name: "testdb",
//    username: "root",
//    password: "root",
//    poolOptions: { maximumPoolSize: 5 },
//    dbOptions: { useSSL: false }
//};
//
//endpoint http:Listener myEP {
//    port: 9090
//};
//
//@http:ServiceConfig {
//    basePath: "/MyDB"
//}
//service<http:Service> MyDB bind myEP {
//    createTable(endpoint outboundEP, http:Request request) {
//        http:Response response = new;
//
//        var selectRet = testDB->select("SELECT * FROM student", ());
//        table dt;
//        match selectRet {
//            table tableReturned => dt = tableReturned;
//            error e => io:println("Select data from student table failed: "
//                    + e.message);
//        }
//
//        io:println("\nConvert the table into json");
//        var jsonConversionRet = <json>dt;
//        match jsonConversionRet {
//            json jsonRes => {
//                response.setJsonPayload(untaint jsonRes);
//
//            }
//            error e => {
//                io:println("Error in table to json conversion");
//                response.setTextPayload("Error in table to json conversion");
//            }
//        }
//        _ = outboundEP->respond(response);
//
//    }
//}

function main(string... args) {
    io:println(composite:getHost(helloEP));
    io:println(composite:getPort(helloEP));
}

