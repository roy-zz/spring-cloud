우리는 [이전 장](https://imprint.tistory.com/212?category=1069520) 에서 Spring Cloud Netflix Eureka에 대해서 알아보았다.
이번 장에서는 Spring Cloud Gateway를 구축하기에 앞서서 API Gateway란 무엇인지에 대해서 알아본다.
모든 소스 코드는 [깃허브 (링크)](https://github.com/roy-zz/spring-cloud) 에 올려두었다.

---

### API Gateway Pattern

#### API Gateway Pattern의 필요성

우리는 이전에 하나의 커다란 서비스가 기능별로 나누어진 마이크로서비스 아키텍처(MSA)에 대해서 알아보았다.
MSA를 적용하여 온라인 상점을 구축하고 있는 상황을 예로 들어본다.
우리는 서비스마다 기능을 나누게 될 것이고 Price Service(상품의 가격을 담당하는 서비스), Order Service(주문을 담당하는 서비스), Review Service(리뷰를 담당하는 서비스)등등 수많은 서비스가 탄생할 것이다.
이렇게 서비스가 기하급수적으로 나뉘었을 때 아래의 이미지와 같이 클라이언트가 직접 담당하는 서비스에게 요청을 보내는 구조라면 어떻게 될까

![](image/direct-client-to-microservice-communication.png)

당장 클라이언트는 내가 원하는 정보를 어떠한 서비스에게 요청해야 되는지 심지어 어떠한 서버가 있는지도 알 수 없다.
우리가 API Gateway를 사용해야하는 이유에는 여러가지가 있다.

1. **결합**: API Gateway가 없으면 클라이언트의 애플리케이션은 서버가 어떠한 구조로 분리되어 있는지 알아야한다. 
Gateway가 없다면 서버의 구조에 따라 애플리케이션이 서로 다른 Endpoint에 요청을 보낼 것이며 이러한 결합도는 추후 서버의 구조가 변경될 때 클라이언트의 애플리케이션도 같이 업데이트가 진행되어야한다.
클라이언트와 서버의 구조가 강하게 결합되어 있다고 볼 수 있다.

2. **너무 많은 왕복**: 대부분의 애플리케이션은 메인 페이지에 많은 정보를 포함하고 있다. 이러한 모든 정보를 만들기 위해서는 여러 마이크로서비스들이 각자 자신이 담당하고 있는 데이터를 가공하고 조립하여 생성된다.
만약 API Gateway가 없다면 클라이언트는 필요한 모든 서비스에게 요청을 보내서 필요한 데이터를 수집하고 가공하여 화면에 표시해야한다.
하지만 API Gateway를 사용한다면 필요한 페이지의 정보를 수집해주는 서비스에게 클라이언트의 요청을 전달하고 해당 서비스는 담당 서비스들을 호출하여 데이터를 조합하고 클라이언트 전달하게 될 것이다.
"결국 모든 서비스에게 요청하는거 아닌가요?"라고 할 수 있다. 물론 맞는 말이다. 하지만 클라이언트가 모든 서비스를 직접 호출하는 것과 중간 서비스가 같은 VPC 또는 Subnet 내부의 서비스에게 요청하는 것은 분명한 차이가 존재한다.

3. **보안**: API Gateway가 존재하면 각각의 마이크로서비스들은 더 이상 외부에 노출되지 않아도 된다. 외부에 노출되지않고 같은 망 또는 클러스터에 존재하는 Gateway에게만 진입점을 요청하더라도 클라이언트는 원하는 결과를 얻을 수 있따.
결국 마이크로서비스에게 다가가기 위한 진입점은 유일하게 Gateway이며 보안상 신경써야하는 부분도 Gateway가 된다.

4. **공통 관심사 처리**: 대부분의 마이크로서비스는 인증 및 인가, SSL 인증과 같은 공통적으로 처리해야하는 관심사들이 있다.
API Gateway가 마이크로서비스들 앞단에 존재한다면 이러한 중복되는 처리들을 한 곳에서 처리할 수 있다.

---

#### API Gateway Pattern

위에서 살펴본 것처럼 MSA 구조에서 API Gateway를 사용하지 않으면 발생할 수 있는 많은 문제들이 있다.
API Gateway Pattern은 마이크로서비스의 특정 그룹의 단일 진입점을 재공하는 패턴이다.
여러 클라이언트 앱과 복잡한 마이크로서비스 기반의 애플리케이션을 디자인하는 경우에 장점을 가지는 패턴이다.

![](image/custom-service-api-gateway.png)

API 게이트웨이는 클라이언트 애플리케이션과 마이크로서비스들 사이에 위치하여 요청을 서비스로 라우팅하는 역방향 프록시로 사용된다.
또한 인증, SSL 종료 및 캐시와 같은 공통 관심사를 처리하는 기능을 제공할 수 있다.
하지만 주의해야할 점이 있다. 
그림에서 여러 종류의 클라이언트가 단일 API 게이트웨이 서비스를 호출하게 되고 API 게이트웨이는 클라이언트들의 요청을 맞추기 위한 로직이 추가될 것이다.
인증, SSL과 같은 공통 관심사를 처리하는 로직도 추가될 것이고 이렇게 시간이 지나다보면 API 게이트웨이는 거대한 모놀로식 서비스처럼 커질 수 있다.(마치 커몬의 저주처럼)
우리는 API Gateway Pattern을 사용할 때 Gateway에 꼭 필요한 로직만 추가할 수 있도록 주의깊게 개발해야한다.

API Gateway가 꼭 하나만 존재해야할 필요는 없으며 클라이언트의 요청과 마이크로서비스의 중간에서 어댑터 역할을 하는 어댑터 패턴의 하나로 사용될 수 있다.
이러한 패턴을 BFF(프런트 엔드의 백엔드)라고 하며 각 클라이언트 애플리케이션에 맞춰진 다른 API를 제공할 수 있다.

![](image/multiple-custom-api-gateways.png)

그림은 클라이언트의 유형에 따라 분리되어 있는 API Gateway를 보여주고 있다.
모바일은 모바일 전용 게이트웨이에 요청을 보내며 SPA(Single Page Application)은 Web용 게이트웨이에 요청을 보낸다.
기존에 사용되던 Client WebApp MVC는 SPA Web과 동일하게 Web용 게이트웨이에 요청을 보낸다.

---

### API Gateway Pattern의 주요 기능

API Gateway Pattern에는 몇가지 주요기능이 있다.

- [역방향 프록시 or 게이트웨이 라우팅](https://docs.microsoft.com/ko-kr/azure/architecture/patterns/gateway-routing): 일반적으로 HTTP 요청을 7계층인 OSI Application Layer에서 내부 마이크로서비스의 엔드포인트로 리디렉션 또는 라우팅하는 역방향 프록시를 제공한다.
Gateway는 클라이언트에게 단일 엔드포인트나 URL을 제공하고 내부의 마이크로서비스 그룹에 매핑하여 라우팅 처리한다. 이렇게 중간에서 Gateway가 라우팅 처리를 해주기 때문에 모놀리식 API 구조에서 마이크로서비스로 리팩터링 하더라도 클라이언트는 URI 변경에 영향을 받지 않는다.

- [요청 집계](https://docs.microsoft.com/ko-kr/azure/architecture/patterns/gateway-aggregation): 모든 요청이 Gateway를 통해서 마이크로서비스로 전달되기 때문에 요청에 대한 집계를 만들 수 있다.
이러한 패턴은 클라이언트 페이지/화면이 여러 마이크로서비스에게 요청한 결과를 가지고 구성될 때 편리하다. 이 방법을 사용하면 클라이언트 앱은 한 번 요청을 보내고 서버 내부에서 여러 마이크로서비스들의 응답결과를 조합해서 받게된다.
이 패턴의 주된 이점과 목적은 클라이언트와 서버 간의 통신을 줄이는데 있다.

- [교차 편집 문제 or 게이트웨이 오프로딩](https://docs.microsoft.com/ko-kr/azure/architecture/patterns/gateway-offloading): 간단하게 말하면 마이크로서비스들 간의 공통된 관심사를 Gateway로 추출할 수 있다는 의미가 된다.
추출하는 로직에 대한 예시는 아래와 같다.
    - 인증 및 권한 부여
    - 서비스 검색 통합
    - 응답 캐싱
    - 정책, 회로 차단기 및 QoS 다시 시도
    - 속도 제한
    - 부하 분산
    - 로깅, 추적, 상관 관계
    - 헤더, 쿼리 문자열 및 청구 변환
    - IP 허용 목록에 추가

---

### API Gateway Pattern의 단점

- 가장 중요한 단점은 API Gateway를 구현할 때 해당 계층을 내부 마이크로서비스와 결합한다는 점이다. 말이 조금 애매한데 Gateway가 거대해질 수 있다는 의미가 된다.
[GOTO 2016](https://www.youtube.com/watch?v=rXi5CLjIQ9k&ab_channel=GOTOConferences) 에서 API Gateway 패턴은 "새로운 ESB"라고 언급했다.
여기서 ESB는 Enterprise Service Bus로 중앙 집중식 소프트웨어 컴포넌트가 백엔드 시스템에 대한 통합을 수행하고 패턴을 의미하는 1990년대 후반에 등장한 아키텍처다.

- API Gateway가 추가되면 문제가 발생할 수 있는 실패 지점이 추가된다.

- API Gateway도 결국 요청을 처리할 수 있는 서비스에게 요청하고 반환하는 역할을 하기 때문에 추가 네트워크 호출로 인한 응답 시간 증가로 이어질 수 있다.

- API Gateway가 충분히 스케일 아웃되어 있지 않으면 마이크로서비스들의 상태와 무관하게 서비스 병목 현상이 발생할 수 있다.

- API Gateway는 각각의 마이크로서비스들과 강하게 결합하고 있기 때문에 새로운 엔드포인트를 노출하기 위해서는 API Gateway를 같이 업데이트해야 한다.

- 위에서 마이크로서비스가 업데이트되면 API Gateway도 같이 업데이트 되야한다는 단점을 알아보았다. 
추가로 만약 이러한 API Gateway를 단일 팀에서 관리하게 되면 개발 속도가 지연되는 개발 지연 현상이 발생할 수 있다.

---

지금까지 Spring Cloud Gateway를 사용하기 위해 API Gateway Pattern에 대해서 알아보았다.
다음 장에서는 직접 Spring Cloud Gateway 서버를 구축해본다.

---

**참고한 자료:** 
- https://docs.microsoft.com/ko-kr/dotnet/architecture/microservices/architect-microservice-container-applications/direct-client-to-microservice-communication-versus-the-api-gateway-pattern
- https://microservices.io/patterns/apigateway.html
- https://www.youtube.com/watch?v=rXi5CLjIQ9k&ab_channel=GOTOConferences