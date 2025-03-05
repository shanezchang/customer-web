package com.shane.customer_web.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserTokenDTO {
    private Long userId;
    private Long expireTimestamp;
}
