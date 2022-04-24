package com.roy.springcloud.userservice.service.impl;

import com.roy.springcloud.userservice.domain.MyUser;
import com.roy.springcloud.userservice.dto.MyUserDto;
import com.roy.springcloud.userservice.repository.MyUserRepository;
import com.roy.springcloud.userservice.service.MyUserService;
import com.roy.springcloud.userservice.vo.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static com.roy.springcloud.util.mapper.MapperUtil.toObject;

@Service
@RequiredArgsConstructor
public class MyUserServiceImpl implements MyUserService {
    private final Environment environment;
    private final RestTemplate restTemplate;
    private final MyUserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void createUser(MyUserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());
        MyUser user = toObject(userDto, MyUser.class);
        user.setEncryptedPassword(passwordEncoder.encode(userDto.getPassword()));
        userRepository.save(user);
    }

    @Override
    public MyUserDto getUserByUserId(String userId) {
        MyUser savedUser = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        ResponseEntity<List<OrderResponse>> orderListResponse = restTemplate.exchange(
                getOrderRequestUrl(userId), HttpMethod.GET, null,
                new ParameterizedTypeReference<List<OrderResponse>>() {}
        );
        MyUserDto response = toObject(savedUser, MyUserDto.class);
        response.setOrders(orderListResponse.getBody());
        return response;
    }

    private String getOrderRequestUrl(String userId) {
        StringBuilder urlPrefix = new StringBuilder(Objects.requireNonNull(environment.getProperty("order.url-prefix")));
        urlPrefix.append(environment.getProperty("order.get-orders-path"));
        return String.format(urlPrefix.toString(), userId);
    }

    @Override
    public List<MyUserDto> getAllUser() {
        Iterable<MyUser> savedUsers = userRepository.findAll();
        List<MyUserDto> response = new ArrayList<>();
        ResponseEntity<List<OrderResponse>> orderListResponse = restTemplate.exchange(
                getOrderRequestUrl("020d79c2-172d-4c3a-9156-8d53f11bfc03"), HttpMethod.GET, null,
                new ParameterizedTypeReference<List<OrderResponse>>() {}
        );
        savedUsers.forEach(user -> {
            response.add(toObject(user, MyUserDto.class));
        });
        return response;
    }

    @Override
    public MyUserDto getUserDetailsByEmail(String email) {
        MyUser savedUser = getMyUserByEmail(email);
        return toObject(savedUser, MyUserDto.class);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        MyUser savedUser = getMyUserByEmail(email);
        return new User(savedUser.getEmail(), savedUser.getEncryptedPassword(),
                true, true, true, true,
                Collections.emptyList()
        );
    }

    private MyUser getMyUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
