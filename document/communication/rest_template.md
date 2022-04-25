이번 장부터는 마이크로서비스들 간의 통신(Communication)에 대해서 알아본다.
이번 장에서는 전통적으로 사용되었던 Rest Template을 통한 통신을 알아본다.
모든 소스 코드는 [깃허브 (링크)](https://github.com/roy-zz/spring-cloud) 에 올려두었다.

---

### 개요

마이크로서비스들 간의 통신은 크게 두 가지로 분류된다.

- 동기 방식(Synchronous)의 HTTP 통신
- 비동기 방식(Asynchronous)의 AMQP를 통한 통신

비동기 방식을 사용하기 전에 우리는 동기 방식을 통한 통신 방법을 다뤄볼 것이다.
동기 방식 통신에도 Rest Template을 사용하는 방법과 넥플릭스의 FeignClient를 사용하는 방법이 있는데 이번 장에서는 Rest Template를 사용하는 방법에 대해서 알아볼 것이다.

지금까지 우리가 구축한 서비스를 살펴보면 사용자의 정보를 다루는 User Service와 주문 정보를 다루는 Order Service가 있었다.
사용자의 정보를 요청할 때 사용자가 주문한 주문 정보까지 필요하다면 요청받은 User Service는 Order Service와의 통신을 통해서 주문 정보를 조회하고 조회된 결과를 사용자 정보에 합쳐서 반환하게 된다.
그림으로 살펴보면 아래와 같다.

![](image/rest-template-communication-diagram.png)

1. 사용자가 ~/user-service/users/{userId} API를 호출하여 사용자 정보를 조회한다.
2. 유저 서비스는 Rest Template을 통하여 디스커버리 서버에게 사용가능한 주문 서비스의 정보를 조회한다.
3. 조회된 주문 서비스에게 사용자의 주문 정보를 요청한다.
4. 유저 서비스는 조회된 주문 정보와 사용자의 정보를 합쳐서 클라이언트에게 반환한다.

---

### User Service 수정

1. RestTemplate 빈 등록

유저 서비스에서 RestTemplate를 사용할 수 있도록 메인 메서드가 있는 클래스 파일에 RestTemplate를 빈으로 등록하는 코드를 추가한다.

```java
@EnableEurekaClient
@SpringBootApplication
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
    // 생략...
    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
```

2. 컨트롤러에 API 추가

유저 서비스의 컨트롤러에 사용자의 정보를 조회하는 API를 추가한다.

```java
@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class MyUserController {
    private final Environment environment;
    private final MyUserService userService;
    // 생략...
    @GetMapping("/users/{userId}")
    public ResponseEntity<MyUserResponse> getUser(@PathVariable("userId") String userId) {
        MyUserDto userDto = userService.getUserByUserId(userId);
        MyUserResponse response = toObject(userDto, MyUserResponse.class);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
```

3. application.yml 파일 수정

application.yml 파일에 주문 서비스의 API를 호출하기 위한 정보를 추가한다.
필자의 경우 Config 서버를 따로 구축하였기 때문에 유저 서비스 프로젝트의 application.yml 파일을 수정한 것이 아니라 Config 서버에서 참조하는 원격 깃 리포지토리의 유저 서비스용 user-default.yml 파일을 수정하였다.
Spring Cloud Config 서버가 구축되어 있지 않다면 필자가 [이전에 작성한 글(링크)](https://imprint.tistory.com/223) 을 참고하거나 유저 서비스 프로젝트의 application.yml 파일에 추가해도 동일하게 작동한다.

![](image/modify-user-default-yml.png)

4. 컨트롤러가 호출하는 서비스 수정

서비스에서 주문 서비스의 API를 호출하여 사용자 정보 DTO에 추가하도록 서비스 코드를 수정한다.

```java
@Service
@RequiredArgsConstructor
public class MyUserServiceImpl implements MyUserService {
    private final Environment environment;
    private final RestTemplate restTemplate;
    private final MyUserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    @Override
    public MyUserDto getUserByUserId(String userId) {
        MyUser savedUser = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        ResponseEntity<List<OrderResponse>> orderListResponse = restTemplate.exchange(
                getOrderRequestUrl(userId), HttpMethod.GET, null,
                new ParameterizedTypeReference<List<OrderResponse>>() {}
        );
        MyUserDto response = toObject(savedUser, MyUserDto.class);
        response.setOrders(orderListResponse.getBody());
        return response;
    }
    private String getOrderRequestUrl(String userId) {
        StringBuilder urlPrefix = new StringBuilder(Objects.requireNonNull(environment.getProperty("order.url-prefix")));
        urlPrefix.append(environment.getProperty("order.get-orders-path"));
        return String.format(urlPrefix.toString(), userId);
    }
}
```

**MyUserDto**

```java
@Data
public class MyUserDto {
    private String email;
    private String password;
    private String name;
    private String userId;
    private LocalDateTime createdAt;
    private String encryptedPassword;
    private List<OrderResponse> orders = new ArrayList<>();
}
```

**OrderResponse**

```java
@Data
public class OrderResponse {
    private String productId;
    private Integer quantity;
    private Integer unitPrice;
    private Integer totalPrice;
    private LocalDateTime createdAt;
    private String orderId;
}
```

5. 주문 서비스 API 추가

사용자의 주문 정보를 DB에 저장하는 Post API와 조회하는 Get API를 추가한다.
이해에 필요한 컨트롤러와 서비스 코드만 첨부하였다. 
리포지토리 및 인터페이스 파일까지 확인하려면 글의 최상단에 있는 필자의 Git 리포지토리에서 확인하도록 한다.

**OrderController**
```java
@RestController
@RequiredArgsConstructor
@RequestMapping("/order-service")
public class OrderController {
    private final Environment environment;
    private final OrderService orderService;
    @PostMapping("/{userId}/orders")
    public ResponseEntity<OrderResponse> createOrder(@PathVariable("userId") String userId,
                                                     @RequestBody OrderSaveRequest request) {
        OrderDto orderDto = toObject(request, OrderDto.class);
        orderDto.setUserId(userId);
        OrderDto savedOrder = orderService.createOrder(orderDto);
        OrderResponse response = toObject(savedOrder, OrderResponse.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<OrderResponse>> getOrder(@PathVariable("userId") String userId) {
        List<OrderDto> savedOrders = orderService.getOrderByUserId(userId);
        List<OrderResponse> response = savedOrders.stream()
                .map(order -> toObject(order, OrderResponse.class))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
```

**OrderServiceImpl**

```java
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    @Override
    public OrderDto createOrder(OrderDto orderDto) {
        orderDto.setOrderId(UUID.randomUUID().toString());
        orderDto.setTotalPrice(orderDto.getQuantity() * orderDto.getUnitPrice());
        Order newOrder = toObject(orderDto, Order.class);
        orderRepository.save(newOrder);
        return toObject(newOrder, OrderDto.class);
    }
    @Override
    public OrderDto getOrderByOrderId(String orderId) {
        Order savedOrder = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot found order"));
        return toObject(savedOrder, OrderDto.class);
    }
    @Override
    public List<OrderDto> getOrderByUserId(String userId) {
        Iterable<Order> savedOrders = orderRepository.findAllByUserId(userId);
        List<OrderDto> response = new ArrayList<>();
        savedOrders.forEach(order -> {
            response.add(toObject(order, OrderDto.class));
        });
        return response;
    }
}
```

이렇게 RestTemplate을 통한 통신은 준비가 완료되었다.
이제 테스트를 통해서 정상작동하는지 확인해보도록 한다.

---

### 테스트

1. 사용자 정보 저장

테스트에 필요한 사용자 정보를 저장하고 결과값 중 userId를 복사해둔다.

![](image/save-user.png)

2. 주문 정보 등록

주문 서비스의 API를 호출하여 주문 정보를 등록한다.
이때 사용되는 userId는 1단계에서 획등한 userId를 사용해야 한다.

![](image/save-order.png)

3. 로그인

사용자의 정보를 조회하는 API는 인증이 필요한 API이므로 로그인을 통해 JWT를 획득한다.

![](image/user-login.png)

4. 사용자 정보 조회

3단계에서 획득한 정보를 헤더에 넣고 1단계에서 획득한 userId를 요청 URL에 포함시켜 사용자 정보를 조회한다.

![](image/get-user-with-orders.png)

**참고**
1. 요청 시 사용된 curl은 아래와 같다.

```bash
$ curl --location --request GET 'http://localhost:8000/user-service/users/f312a60f-e24d-4862-933b-97add5269f0c' \
--header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI2NGI1NDhlZC04MzViLTRlNjUtOWEyMS02NTM2MmEwNDEzMzgiLCJleHAiOjE2NTE2NzcwMTV9.Y6tdlV3Z1moDWpJ6iV8KisKXCVg9zoBmiowEl6Jkr8N8m4v5yZfysXlMBy598uGpBHbgCS2i27hFUUUUoYGo1w' \
--header 'Cookie: JSESSIONID=63BF99CFFAF313D37DB45A38641D7228'
```

2. 변동되는 IP 대응

우리는 설정정보에 주문 서비스의 IP주소를 적어두었다.

```yaml
order:
  url-prefix: http://127.0.0.1:8000/order-service
  get-orders-path: /%s/orders 
```

하지만 설정 정보에 이렇게 적어놓는 경우 변동되는 서버 IP를 대응할 수 없으며 Scale-Out 되어 새로운 IP가 추가되어도 적용할 수 없다.
이전에 LoadBalancer에서 사용한 것과 같이 IP + Port 조합이 아닌 마이크로서비스의 이름으로 설정 정보를 구성할 수 있다.

RestTemplate를 빈으로 등록하는 단계에서 @LoadBalancer라는 애노테이션을 추가한다.

```java
@Bean
@LoadBalanced
public RestTemplate getRestTemplate() {
    return new RestTemplate();
}
```

설정정보에 IP + Port 로 구성된 주소 부분을 마이크로서비스 이름으로 변경한다.

```yaml
order:
  url-prefix: http://order-service/order-service
  get-orders-path: /%s/orders 
```

이렇게 구성하는 경우 IP가 변동되거나 Scale-Out되어 새로운 IP가 추가되더라도 우리는 별다른 작업없이 적용이 가능하다.

---

지금까지 Rest Template을 사용하여 마이크로서비스간 통신하는 방법에 대해서 알아보았다.
다음 장에서는 FeignClient를 사용하여 마이크로서비스간 통신하는 방법에 대해서 알아본다.

---

**참고한 강의:**

- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%ED%81%B4%EB%9D%BC%EC%9A%B0%EB%93%9C-%EB%A7%88%EC%9D%B4%ED%81%AC%EB%A1%9C%EC%84%9C%EB%B9%84%EC%8A%A4