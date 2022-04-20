package com.roy.springcloud.userservice.vo.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class UserSaveRequest {
    @Email
    @NotNull(message = "Email cannot be blank")
    @Size(min = 2, message = "Email not be less than two characters")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must be equal or grater than 8 characters and less than 16 characters")
    private String password;

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, message = "Name not be less than two characters")
    private String name;
}
