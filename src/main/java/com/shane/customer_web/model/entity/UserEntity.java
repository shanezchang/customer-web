package com.shane.customer_web.model.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@Data
@TableName("t_user")
@Builder
public class UserEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String account;

    private String nickname;

    private String email;

    private String password;

    private Long createTimestamp;

    private Long updateTimestamp;

}
