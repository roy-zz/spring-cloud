package com.roy.springcloud.testserver1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/test-server-1")
public class TestController {

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to the Test Server - 1";
    }

}
