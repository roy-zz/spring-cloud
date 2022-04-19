이번 장에서는 [API Gateway - 이론]()에 이어 직접 Spring Gateway 서버를 구축해본다.
모든 소스 코드는 [깃허브 (링크)](https://github.com/roy-zz/spring-cloud) 에 올려두었다.

---

### 프로젝트 구성

Spring Gateway 서버를 구축하기 위해서는 Discovery 서버가 실행되어 있어야한다.
만약 실행되지 않은 상태라면 필자가 [이전에 작성한 글 (링크)]() 를 참고하도록 한다.

---

**1. 의존성 추가**

Spring Gateway 서버를 구축하기 위해 필요한 의존성이므로 전부 추가하도록 한다.

```bash
ext {
    set('springCloudVersion', "2021.0.1")
}

dependencies {
    implementation 'org.springframework.cloud:spring-cloud-starter-gateway'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

dependencyManagement {
    imports {
        mavenBom "org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}"
    }
}
```