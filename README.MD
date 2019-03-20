# Reactive loans checker
### Simple bot for marketplace loans endpoint

Java spring-boot bot that downloads new marketplace loans from [Zonky API](https://api.zonky.cz/loans/marketplace) in regullar intervals.
By default it will take loans for last 24 hours and then do regular check each 5 minutes.

Demonstrate using of Spring 5 with reactive streams ([ProjectReactor](https://projectreactor.io/)) and [Spring WebFlux](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html) reactive client to obtain data and process them.

#### Technology stack
- Java 8
- Spring Boot 2.x
- Spring WebFlux
- Scheduling Tasks
- Project Reactor
- Mockito
- WireMock


#### How to build
```
  mvn clean install
  ```
 
#### How to run
  ```
  mvn spring-boot:run
  ```