package com.roy.springcloud.testserver1.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/test-server-1")
public class TestController {

    @GetMapping("/welcome")
    public String welcome(@RequestHeader("test-server-1-request") String requestHeader) {
        log.info("{}", requestHeader);
        return "Welcome to the Test Server - 1";
    }

    @GetMapping("/custom-filter")
    public String customFilter() {
        return "Custom filter with test server 1";
    }

}
