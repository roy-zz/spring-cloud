package com.roy.springcloud.userservice.service.impl;

import com.roy.springcloud.userservice.domain.User;
import com.roy.springcloud.userservice.dto.UserDto;
import com.roy.springcloud.userservice.repository.UserRepository;
import com.roy.springcloud.userservice.service.UserService;
import com.roy.springcloud.util.mapper.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());
        User user = MapperUtil.toObject(userDto, User.class);
        user.setEncryptedPassword("encrypted_password");
        userRepository.save(user);
        return userDto;
    }
}
