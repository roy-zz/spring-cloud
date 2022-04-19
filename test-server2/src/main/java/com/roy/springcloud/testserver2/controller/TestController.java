package com.roy.springcloud.testserver2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/test-server-2")
public class TestController {

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to the Test Server - 2";
    }

}
