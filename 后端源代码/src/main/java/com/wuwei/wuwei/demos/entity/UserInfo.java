package com.wuwei.wuwei.demos.entity;

import lombok.Data;

import java.util.Objects;

@Data
public class UserInfo {
    String openid;
    String token;
    String avatarUrl;
    String nickName;

}
