# API Proxy

`$ git clone https://github.com/dvsmedeiros/api-proxy.git`

`$ cd api-proxy`

`$ ./mvnw clean install`

`$ java -jar api-proxy-0.0.1-SNAPSHOT.jar`

####Configure host and host to redirect in ./config/config.properties

```
// for redirect all requests use / 
// for specific context use /foo/bar
server.servlet.context-path=<context to redirect>
// 127.0.0.1
server.address=<your ip host>
// 8080
server.port=<your port>       
//192.168.1.67
server.redirect=<redirect ip host>
// 80
server.port.redirect=<redirect port host>
```
