package com.roy.springcloud.userservice.controller;

import com.roy.springcloud.userservice.dto.UserDto;
import com.roy.springcloud.userservice.service.UserService;
import com.roy.springcloud.userservice.vo.request.UserSaveRequest;
import com.roy.springcloud.userservice.vo.response.UserSaveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.roy.springcloud.util.mapper.MapperUtil.toObject;

@RestController
@RequestMapping("/user-service")
@RequiredArgsConstructor
public class UserController {
    private final Environment environment;
    private final UserService userService;

    @GetMapping("/health-check")
    public String healthCheck() {
        return "User service is working";
    }

    @GetMapping("/welcome")
    public String welcome() {
        return environment.getProperty("greeting.message");
    }

    @PostMapping("/users")
    public ResponseEntity<UserSaveResponse> createUser(@RequestBody UserSaveRequest request) {
        UserDto userDto = toObject(request, UserDto.class);
        userService.createUser(userDto);
        UserSaveResponse response = toObject(userDto, UserSaveResponse.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
