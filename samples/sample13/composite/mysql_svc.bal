import ballerinax/composite;
import ballerina/io;

endpoint composite:Listener helloEP {
    port: 3306,
    host: "localhost"
};

//@composite:ContainerConfig {
//
//}
//service <composite:Container> helloWorld bind helloEP {}
