package com.roy.springcloud.userservice.vo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MyUserResponse {
    private String email;
    private String name;
    private String userId;

    private List<OrderResponse> orders;
}
