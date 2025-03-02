package com.shane.customer_web.model.bo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignInBO {

    @NotBlank
    private String account;

    @NotBlank
    private String signature;

}
