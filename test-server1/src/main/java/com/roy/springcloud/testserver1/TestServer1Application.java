package com.roy.springcloud.testserver1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class TestServer1Application {

    public static void main(String[] args) {
        SpringApplication.run(TestServer1Application.class, args);
    }

}
