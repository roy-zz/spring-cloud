package com.roy.springcloud.testserver2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class TestServer2Application {

    public static void main(String[] args) {
        SpringApplication.run(TestServer2Application.class, args);
    }

}
