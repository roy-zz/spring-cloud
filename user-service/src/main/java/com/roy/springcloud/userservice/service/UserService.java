package com.roy.springcloud.userservice.service;

import com.roy.springcloud.userservice.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    void createUser(UserDto userDTO);
    UserDto getUserByUserId(String userId);
    UserDto getUserDetailsByEmail(String email);
    List<UserDto> getAllUser();
}
