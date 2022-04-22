package com.roy.springcloud.userservice.controller;

import com.roy.springcloud.userservice.dto.MyUserDto;
import com.roy.springcloud.userservice.service.MyUserService;
import com.roy.springcloud.userservice.vo.request.MyUserSaveRequest;
import com.roy.springcloud.userservice.vo.response.MyUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.roy.springcloud.util.mapper.MapperUtil.toObject;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class MyUserController {
    private final Environment environment;
    private final MyUserService userService;

    @GetMapping("/health-check")
    public String healthCheck() {
        return String.format("It's Working in User Service on PORT: %s", environment.getProperty("local.server.port"));
    }

    @GetMapping("/welcome")
    public String welcome() {
        return environment.getProperty("greeting.message");
    }

    @PostMapping("/users")
    public ResponseEntity<MyUserResponse> createUser(@RequestBody MyUserSaveRequest request) {
        MyUserDto userDto = toObject(request, MyUserDto.class);
        userService.createUser(userDto);
        MyUserResponse response = toObject(userDto, MyUserResponse.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/users")
    public ResponseEntity<List<MyUserResponse>> getUsers() {
        List<MyUserDto> savedUsers = userService.getAllUser();
        List<MyUserResponse> response = new ArrayList<>();
        savedUsers.forEach(user -> {
            response.add(toObject(user, MyUserResponse.class));
        });
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<MyUserResponse> getUser(@PathVariable("userId") String userId) {
        MyUserDto userDto = userService.getUserByUserId(userId);
        MyUserResponse response = toObject(userDto, MyUserResponse.class);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
