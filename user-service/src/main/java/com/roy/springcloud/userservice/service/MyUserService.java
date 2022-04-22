package com.roy.springcloud.userservice.service;

import com.roy.springcloud.userservice.dto.MyUserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface MyUserService extends UserDetailsService {
    void createUser(MyUserDto userDTO);
    MyUserDto getUserByUserId(String userId);
    MyUserDto getUserDetailsByEmail(String email);
    List<MyUserDto> getAllUser();
}
