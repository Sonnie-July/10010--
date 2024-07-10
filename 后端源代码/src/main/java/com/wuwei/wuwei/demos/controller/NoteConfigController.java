package com.wuwei.wuwei.demos.controller;

import com.wuwei.wuwei.demos.mapper.UserMapper;
import com.wuwei.wuwei.demos.utils.JWTutil;
import com.wuwei.wuwei.demos.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NoteConfigController {

    @Autowired
    UserMapper userMapper;

    @RequestMapping("/configAppNote")
    public Result<String> configAppNote(@RequestParam("noteUrl") String noteUrl,@RequestParam("token") String token){
        try {
            String openid = JWTutil.getClaimByToken(token).get("openid", String.class);
            userMapper.configAppNote(openid,noteUrl);
            return new Result<>("配置成功",200);
        }catch (Exception E){
            return new Result<>("wrong",500);
        }

    }
}
