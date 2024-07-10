package com.wuwei.wuwei.demos.mapper;

import com.wuwei.wuwei.demos.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

     String getUserToken(@Param("openid") String openid);

     int insertUserInfo(@Param("openid") String openid,
                              @Param("token") String token,
                              @Param("avatarUrl") String avatarUrl,
                              @Param("nickName") String nickName);

    UserInfo getUserInfo(@Param("openid") String openid);

    void configAppNote(@Param("openid") String openid,@Param("noteUrl") String noteUrl);
}
