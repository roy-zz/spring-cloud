이번 장에서는 [Spring Cloud Gateway - 구성](https://imprint.tistory.com/215) 에 이어 직접 API Gateway에 Filter를 적용하는 방법들에 대해서 알아본다.
모든 소스 코드는 [깃허브 (링크)](https://github.com/roy-zz/spring-cloud) 에 올려두었다.

---

### 자바 코드로 적용

1. Gateway 모듈에 구성을 위한 FilterConfig 클래스 파일을 생성한다.

"/test-server-1/**" 경로로 들어오는 요청의 헤더에 "test-server-1-request" 키와 "test-server-1-request-header" 값을 가지는 헤더를 추가하고 TEST-SERVER-1 이라는 이름을 가진 애플리케이션에 리다이렉트 한다.
"/test-server-2/**" 경로로 들어오는 요청의 헤더에 "test-server-2-request" 키와 "test-server-2-request-header" 값을 가지는 헤더를 추가하고 TEST-SERVER-2 라는 이름을 가진 애플리케이션에 리다이렉트 한다.

```java
@Configuration
public class FilterConfig {
    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/test-server-1/**")
                            .filters(f -> f.addRequestHeader("test-server-1-request", "test-server-1-request-header")
                                            .addResponseHeader("test-server-1-response", "test-server-1-response-header"))
                        .uri("lb://TEST-SERVER-1"))
                .route(r -> r.path("/test-server-2/**")
                            .filters(f -> f.addRequestHeader("test-server-2-request", "test-server-2-request-header")
                                            .addResponseHeader("test-server-2-response", "test-server-2-response-header"))
                        .uri("lb://TEST-SERVER-2"))
                .build();
    }
}
```

2. Eureka Client의 컨트롤러를 수정한다.

헤더의 정보를 출력하도록 test-server1, test-server2 모듈의 컨트롤러를 수정한다.

**test-server1**
```java
@Slf4j
@RestController
@RequestMapping(value = "/test-server-1")
public class TestController {
    @GetMapping("/welcome")
    public String welcome(@RequestHeader("test-server-1-request") String requestHeader) {
        log.info("{}", requestHeader);
        return "Welcome to the Test Server - 1";
    }
}
```

**test-server2**
```java
@Slf4j
@RestController
@RequestMapping(value = "/test-server-2")
public class TestController {
    @GetMapping("/welcome")
    public String welcome(@RequestHeader("test-server-2-request") String requestHeader) {
        log.info("{}", requestHeader);
        return "Welcome to the Test Server - 2";
    }
}
```

브라우저를 켜고 localhost:8000/test-server-1/welcome과 localhost:8000/test-server-2/welcome에 접속해본다.
각 모듈의 출력 결과는 아래와 같다.
**test-server1**
```bash
INFO 85192 --- [o-auto-1-exec-1] c.r.s.t.controller.TestController        : test-server-1-request-header
```
**test-server2**
```bash
INFO 85202 --- [o-auto-1-exec-1] c.r.s.t.controller.TestController        : test-server-2-request-header
```

Response 헤더에도 정상적으로 입력되었는지 브라우저에서 개발자 모드를 실행시키고 확인해본다.

![](image/check-custom-header.png)

만약 자바 코드로 filter를 적용하고 싶지 않다면?!
아래와 같이 application.yml 파일을 수정해서 필터를 적용해도 동일하게 작동한다.

```yaml
spring:
  application:
    name: gateway-service
  cloud:
    gateway:
      routes:
        - id: test-server-1
          uri: lb://TEST-SERVER-1
          predicates:
            - Path=/test-server-1/**
          filters:
            - AddRequestHeader=test-server-1-request, test-server-1-request-header
            - AddResponseHeader=test-server-1-response, test-server-1-response-header
        - id: test-server-2
          uri: lb://TEST-SERVER-2
          predicates:
            - Path=/test-server-2/**
          filters:
            - AddRequestHeader=test-server-2-request, test-server-2-request-header
            - AddResponseHeader=test-server-2-response, test-server-2-response-header
```

---

### Custom Filter

1. CustomFilter 클래스를 생성한다.

AbstractGatewayFilterFactory를 상속받은 클래스를 생성한다.
apply메서드를 확인해보면 요청처리 전에 필터를 처리하는 부분과 요청처리 후에 필터를 처리하는 부분이 나뉘어 있는 것을 확인할 수 있다.

```java
@Slf4j
@Component
public class CustomFilter extends AbstractGatewayFilterFactory<CustomFilter.Config> {
    public CustomFilter() {
        super(Config.class);
    }
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Pre process start ====================================================
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            log.info("Custom pre process filter: request uri -> {}", request.getId());
            // Pre process end =======================================================
            // Post process start =======================================================
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("Custom post process filter: response code -> {}", response.getStatusCode());
            }));
            // Post process end =======================================================
        };
    }
    public static class Config {
        // Put the configuration properties
    }
}
```

2. application.yml 수정

우리가 생성한 CustomFilter로 등록될 수 있도록 filters 항목에 CustomFilter를 등록한다.

```yaml
spring:
  application:
    name: gateway-service
  cloud:
    gateway:
      routes:
        - id: test-server-1
          uri: lb://TEST-SERVER-1
          predicates:
            - Path=/test-server-1/**
          filters:
            - CustomFilter
        - id: test-server-2
          uri: lb://TEST-SERVER-2
          predicates:
            - Path=/test-server-2/**
          filters:
            - CustomFilter
```

3. 유레카 클라이언트의 컨트롤러를 수정한다.

Filter 연동을 위해 수정한 부분은 없으며 단지 이전 테스트와 컨트롤러가 겹치는 것을 방지하기 위함이다.

**test-server-1**
```java
@Slf4j
@RestController
@RequestMapping(value = "/test-server-1")
public class TestController {
    @GetMapping("/custom-filter")
    public String customFilter() {
        return "Custom filter with test server 1";
    }
}
```

**test-server-2**
```java
@Slf4j
@RestController
@RequestMapping(value = "/test-server-2")
public class TestController {
    @GetMapping("/custom-filter")
    public String customFilter() {
        return "Custom filter with test server 2";
    }
}
```

4. 정상작동 확인

브라우저를 켜고 localhost:8000/test-server-1/custom-filter와 localhost:8000/test-server-2/custom-filter에 접속해본다.
출력된 결과는 아래와 같다.

```bash
c.r.s.gateway.filter.CustomFilter        : Custom pre process filter: request uri -> baf12948-2
c.r.s.gateway.filter.CustomFilter        : Custom post process filter: response code -> 200 OK
c.r.s.gateway.filter.CustomFilter        : Custom pre process filter: request uri -> baf12948-3
c.r.s.gateway.filter.CustomFilter        : Custom post process filter: response code -> 200 OK
```

정상적으로 우리가 원하는 결과가 출력되는 것을 확인할 수 있다.
~~(설마 c.r.s가 com.roy.springcloud의 약자인가...왜 처음알았지)~~

---





---

참고한 강의: https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%ED%81%B4%EB%9D%BC%EC%9A%B0%EB%93%9C-%EB%A7%88%EC%9D%B4%ED%81%AC%EB%A1%9C%EC%84%9C%EB%B9%84%EC%8A%A4