package com.roy.springcloud.userservice.service.impl;

import com.roy.springcloud.userservice.domain.User;
import com.roy.springcloud.userservice.dto.UserDto;
import com.roy.springcloud.userservice.repository.UserRepository;
import com.roy.springcloud.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.roy.springcloud.util.mapper.MapperUtil.toObject;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());
        User user = toObject(userDto, User.class);
        user.setEncryptedPassword(passwordEncoder.encode(userDto.getPassword()));
        userRepository.save(user);
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        User savedUser = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return toObject(savedUser, UserDto.class);
    }

    @Override
    public List<UserDto> getAllUser() {
        Iterable<User> savedUsers = userRepository.findAll();
        List<UserDto> response = new ArrayList<>();
        savedUsers.forEach(user -> {
            response.add(toObject(user, UserDto.class));
        });
        return response;
    }
}
