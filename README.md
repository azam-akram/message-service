# Message service


### Run application without docker container

- First, run the consul service in container
```bash
docker run -p 8500:8500 consul-latest
```
Go to http://localhost:8500 and see consul service is up and running.
- Second, the message service fetches the uuid from uuid-generator service. so uuid-generator service must be available in consul before message-service fetch the uuid.
For this, go to uuid-generator project and build the whole project
```bash
gradlew clean build
```
and then run the GeneratorApplication.
Go to http://localhost:8500 and see new uuid-generator service is added to the dashboard.
- Then come back here and build the whole message-service projects and tests
```bash
gradlew clean build
```
- Now run MessageApplication, which starts Tomcat at port 9999. Go to http://localhost:8500 and see new message-service service is added to the dashboard.

### Application Consul configuration

In order to make service discoverable in consul, use spring cloud dependency and used @EnableDiscoveryClient,
```java
@EnableDiscoveryClient
@SpringBootApplication
public class MessageApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessageApplication.class, args);
    }

}
```

and added consul properties in bootstrap.yml files
```yml
spring:
  application:
    name: message-service
  cloud:
    consul:
      host: consul
      port: 8500
      discovery:
        enabled: true
        prefer-ip-address: true
        health-check-path: /health
        instance-id: ${spring.application.name}:${random.value}
        health-check-interval: 15s
```

### REST Controller
Call REST APIs,

Get All messages
```
HTTP Header: 
Accept: application/json
GET: http://{host-ip}:9999/message-service/v1/message
```

Get Last N number of message messages:
HTTP Header: 
Accept: application/json
```
HTTP Header: 
Accept: application/json
Get: http://{host-ip}:9999/message-service/v1/message/{count}
```

Create new message
```
HTTP Header: 
Accept: application/json
Content-Type: application/json
POST: http://{host-ip}:9999/message-service/v1/message/
body:
{
	"sender": "a sender"
}
```

Update a message
```
HTTP Header: 
Accept: application/json
Content-Type: application/json
PUT: http://{host-ip}:9999/message-service/v1/message/{message-key}
body:
{
	"sender": "a sender"
}
```

Delete older messages
```
DELET: http://{host-ip}:9999/message-service/v1/message/
```

### Build and run docker images
- Go to uuid-generator project and build the docker image by docker-compose. I just build the uuid-generator image there.
  Use docker-compose in uuid-generator project to build uuid-generator image,
  ```bash
  docker-compose build
  ```
  now the uuid-generator image should be available in image list,
  ```bash
  docker-compose build
  ```
- Then come back here in message-service project.
- Build message-service image by,
  ```bash
    docker-compose build
  ```
- And finally run both uuid-generator and message-service by,
  ```bash
    docker-compose up
  ```
Following is the docker-compose.yml
```yaml
 version: '2.2'

services:
  consul:
    image: consul:latest
    ports:
    - "8400:8400"
    - "8500:8500"
    - "8600:8600"

  uuid-generator:
    image: uuid-generator
    ports:
    - "8888:8888"
    depends_on:
    - consul
    links:
    - "consul"

  message-service:
    image: message-service
    build:
      context: ./
      dockerfile: Dockerfile
    ports:
    - "9999:9999"
    depends_on:
    - consul
    links:
    - "consul"
```

### To do
