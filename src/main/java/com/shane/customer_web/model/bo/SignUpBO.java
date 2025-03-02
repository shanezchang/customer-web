package com.shane.customer_web.model.bo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignUpBO {

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String verifyCode;

}
