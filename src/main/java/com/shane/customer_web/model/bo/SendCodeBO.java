package com.shane.customer_web.model.bo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendCodeBO {

    @NotBlank
    private String email;

}
