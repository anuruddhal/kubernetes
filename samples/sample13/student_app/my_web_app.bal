@kubernetes:Service{
    serviceType: "NodePort"
}
endpoint docker:Container game_ep {
    port: 80,
    host: "twenty-forty",
    image: "heyawhite/twentyfortyeight"
};