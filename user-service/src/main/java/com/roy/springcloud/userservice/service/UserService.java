package com.roy.springcloud.userservice.service;

import com.roy.springcloud.userservice.dto.UserDto;

import java.util.List;

public interface UserService {
    void createUser(UserDto userDTO);
    UserDto getUserByUserId(String userId);
    List<UserDto> getAllUser();
}
