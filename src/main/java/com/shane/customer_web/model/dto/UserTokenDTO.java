package com.shane.customer_web.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserTokenDTO {

    private Long userId;

    private Long expireTimestamp;

}
