/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wuwei.wuwei.demos.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wuwei.wuwei.demos.utils.AsyncThread;
import com.wuwei.wuwei.demos.utils.GpsTransform;
import com.wuwei.wuwei.demos.vo.Result;
import com.wuwei.wuwei.demos.dto.wxOuthBack;
import com.wuwei.wuwei.demos.entity.Device;
import com.wuwei.wuwei.demos.entity.UserInfo;
import com.wuwei.wuwei.demos.mapper.DeviceMapper;
import com.wuwei.wuwei.demos.mapper.UserMapper;
import com.wuwei.wuwei.demos.utils.FeignWxLogin;
import com.wuwei.wuwei.demos.utils.JWTutil;
import com.wuwei.wuwei.demos.vo.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
@Scope("prototype")
public class UserController {

    @Value("${wx.appid}")
    String appid;

    @Value("${wx.appsecret}")
    String secret;


    @Autowired
    FeignWxLogin feignWxLogin;

    @Autowired
    UserMapper userMapper;

    @Autowired
    DeviceMapper deviceMapper;

    @GetMapping("/test")
    public Result<String> test() throws  ExecutionException, InterruptedException, TimeoutException {
        System.out.println(deviceMapper.getDevice("odoiU5CcaqZL3u32_2YELtHaA1pY"));
        return new Result<>(JWTutil.getToken("odoiU5EZdJaehFioRwkIHPsnjWV4"),500);
    }

    @GetMapping("/loginweb")
    public Result<String> loginweb(@RequestParam("username") String username,
                                   @RequestParam("password") String password) throws JsonProcessingException {
       try{
           if (username.equals("admin")&&password.equals("admin")){
               String token = "eyJ0eXBlIjoiSldUIiwiYWxnIjoiSFM1MTIifQ.eyJvcGVuaWQiOiJvZG9pVTVFWmRKYWVoRmlvUndrSUhQc25qV1Y0IiwiaWF0IjoxNzE2MDE1MTAyfQ.uchXISB0god31TPjLMrH0GInEiraTSIuO7LKhEAiPaR05iorxzLjLMk1yoqqZ0igwEvXKYtWUYFsLrhPDSW45g";
               return new Result<>(token,200);
           } else if (username.equals("1400698759@qq.com")&&password.equals("Zdf112233@") ){
               String token = "eyJ0eXBlIjoiSldUIiwiYWxnIjoiSFM1MTIifQ.eyJvcGVuaWQiOiJvZG9pVTVFWmRKYWVoRmlvUndrSUhQc25qV1Y0IiwiaWF0IjoxNzE2MDE1MTAyfQ.uchXISB0god31TPjLMrH0GInEiraTSIuO7LKhEAiPaR05iorxzLjLMk1yoqqZ0igwEvXKYtWUYFsLrhPDSW45g";
               return new Result<>(token,200);
           } else{
               return new Result<>("账号或密码错误",500);
           }
       }catch (Exception e){
           return new Result<>("fail",500);
       }

    }

    @GetMapping("/login")
    public Result<String> login(@RequestParam String code) throws JsonProcessingException {
        String res = feignWxLogin.wxlogin(code, appid, secret);
        ObjectMapper mapper = new ObjectMapper();
        //反序列化
        wxOuthBack outhBack = mapper.readValue(res, wxOuthBack.class);
        //拿到openid,该openid加密,转为的token是固定不变的

        String preToken = userMapper.getUserToken(outhBack.openid);
        if (preToken != null) {
            //如果库中有token,直接返回库中以前的token(老用户)
            return new Result<>(preToken, 200);
        } else{
            return new Result<>(JWTutil.getToken(outhBack.openid), 201);
        }
    }

    @GetMapping("/checkLogin")
    public Result<String> checkLogin(@RequestParam("token") String token) {
        try {
            System.out.println("有token的用户发起了校验:" + token);
            String openid = JWTutil.getClaimByToken(token).get("openid", String.class);
            String userToken = userMapper.getUserToken(openid);
            if (userToken.equals(token)) {
                return new Result<>("success", 200);
            } else {
                return new Result<>("wrong", 400);
            }
        } catch (Exception e) {
            return new Result<>("fail", 500);
        }
    }

    /**
     * 新用户走的提交用户信息路线
     */

    @RequestMapping("/submitUserInfo")
    public Result<String> submitUserInfo(@RequestParam("avatarUrl") String avatarUrl,
                                         @RequestParam("nickName") String nickName,
                                         @RequestParam("token") String token
    ) {
        try {
            String openid = JWTutil.getClaimByToken(token).get("openid",String.class);

            userMapper.insertUserInfo(openid,token,avatarUrl,nickName);
        }catch (Exception e){
            return new Result<>("JWT_err",500);
        }
        //加入新用户
        return new Result<>("join success",200);
    }

    @GetMapping("/getUserInfo")
    public Result getUserInfo(@RequestParam("token") String token) {
        try {
            String openid = JWTutil.getClaimByToken(token).get("openid",String.class);
            UserInfo userInfo = userMapper.getUserInfo(openid);
            ArrayList<Device> devices = deviceMapper.getDevices(openid);
            System.out.println(devices);
            System.out.println(userInfo);
            return new Result<>(new UserInfoVo(devices,userInfo.getAvatarUrl(),userInfo.getNickName()),200);
        }catch (Exception e){
            return new Result<>(e.toString(),500);
        }

    }

}
