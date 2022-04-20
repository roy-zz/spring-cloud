package com.roy.springcloud.userservice.service;

import com.roy.springcloud.userservice.dto.UserDto;

public interface UserService {
    UserDto createUser(UserDto userDTO);
}
