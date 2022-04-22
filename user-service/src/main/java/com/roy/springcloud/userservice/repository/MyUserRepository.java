package com.roy.springcloud.userservice.repository;

import com.roy.springcloud.userservice.domain.MyUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface MyUserRepository extends CrudRepository<MyUser, Long> {
    Optional<MyUser> findByUserId(String userId);
    Optional<MyUser> findByEmail(String email);
    Iterable<MyUser> findAll();
}
