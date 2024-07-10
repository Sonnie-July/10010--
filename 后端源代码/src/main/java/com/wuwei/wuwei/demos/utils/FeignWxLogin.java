package com.wuwei.wuwei.demos.utils;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient( name = "feign-weixin",url = "https://api.weixin.qq.com/sns")
public interface FeignWxLogin {

    @RequestMapping(value = "/jscode2session?appid=" + "{appid}" +
            "&secret=" + "{secret}" +
            "&js_code=" + "{code}" + "&grant_type=authorization_code"
            , method = RequestMethod.GET)
    String wxlogin(@RequestParam("code") String code,
                       @RequestParam("appid") String appid,
                       @RequestParam("secret") String secret);


}
