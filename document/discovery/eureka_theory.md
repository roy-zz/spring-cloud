이번 장에서는 Spring Cloud Netflix Eureka(이하 유레카)에 대해서 알아보고 직접 유레카 서버를 구축해본다.

### Spring Cloud Netflix Eureka

마이크로서비스 아키텍처에서 유레카란 client-side service discovery라고 할 수 있다.
시스템은 트래픽에 따라 동적으로 늘어날 수도 줄어들 수도 있다. 
이러한 환경에서 서비스의 host와 port가 동적으로 변하더라도 서비스 인스턴스를 호출할 수 있도록 해주는 Service Registry를 제공 및 관리해준다.

#### 설계 목적

유레카는 middle-tier load balancer로 정의된다.
middle-tier load balancer는 로드밸런싱과 장애복구(failover)가 가능한 middle-tier 서비스 환경을 구성 했을 때 클라이언트(추후에 살펴볼 API Gateway 또는 서비스간 통신)에게 사용 가능한 서비스의 위치 정보를 동적으로 제공할 수 있어야 한다.
전통적인 로드밸런싱의 경우 서비스의 위치가 고정되어 있었지만 AWS와 같은 클라우드 환경에서는 서버의 위치가 동적으로 변동되기 때문에 개발자가 직접 이를 컨트롤 하기는 쉽지 않다.
AWS에서는 middle-tier load balancer를 제공하지 않기 때문에 유레카는 더 많은 관심을 받고 있다.

#### 잘 설계된 유레카 서버 구조

![](image/eureka-server-architecture.png)

AWS 기준으로 가용지역마다 유레카 서버가 복제되어 있다.
서버가 복제되어 있기 때문에 일부 유레카 서버가 중지되더라도 서비스 전체가 중지되는 현상은 발생하지 않는다.

#### 용어 정리

* Service Registration: Client(마이크로서비스)가 자신의 정보를 유레카에 등록하는 행동을 의미한다.

* Service Registry: Client의 정보들(목록, 가용 Client의 위치)을 저장하는 위치를 의미한다.

* Service Discovery: 클라이언트가 Service Registry에서 요청을 보내야하는 대상을 찾는 과정을 의미한다.






---

Spring Cloud Netflix Eureka에 대한 설명의 아래의 글을 재해석 하였음.

- 참고한 자료: https://coe.gitbook.io/guide/