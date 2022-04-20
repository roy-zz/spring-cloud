package com.roy.springcloud.userservice.vo.response;

import lombok.Data;

@Data
public class UserSaveResponse {
    private String email;
    private String name;
    private String userId;
}
