package com.roy.springcloud.userservice.controller;

import com.roy.springcloud.userservice.dto.UserDto;
import com.roy.springcloud.userservice.service.UserService;
import com.roy.springcloud.userservice.vo.request.UserSaveRequest;
import com.roy.springcloud.userservice.vo.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.roy.springcloud.util.mapper.MapperUtil.toObject;

@RestController
@RequestMapping("/user-service")
@RequiredArgsConstructor
public class UserController {
    private final Environment environment;
    private final UserService userService;

    @GetMapping("/health-check")
    public String healthCheck() {
        return String.format("It's Working in User Service on PORT: %s", environment.getProperty("local.server.port"));
    }

    @GetMapping("/welcome")
    public String welcome() {
        return environment.getProperty("greeting.message");
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserSaveRequest request) {
        UserDto userDto = toObject(request, UserDto.class);
        userService.createUser(userDto);
        UserResponse response = toObject(userDto, UserResponse.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getUsers() {
        List<UserDto> savedUsers = userService.getAllUser();
        List<UserResponse> response = new ArrayList<>();
        savedUsers.forEach(user -> {
            response.add(toObject(user, UserResponse.class));
        });
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable("userId") String userId) {
        UserDto userDto = userService.getUserByUserId(userId);
        UserResponse response = toObject(userDto, UserResponse.class);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
