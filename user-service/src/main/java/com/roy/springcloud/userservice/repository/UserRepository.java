package com.roy.springcloud.userservice.repository;

import com.roy.springcloud.userservice.domain.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
