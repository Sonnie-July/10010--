package com.wuwei.wuwei.demos.controller;

import com.wuwei.wuwei.demos.entity.WarnData;
import com.wuwei.wuwei.demos.services.WarnDataService;
import com.wuwei.wuwei.demos.utils.JWTutil;
import com.wuwei.wuwei.demos.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WarnDataController {

    @Autowired
    WarnDataService warnDataService;

    /**
     * 查询用户的设备报警记录
     *
     * @param token
     * @return
     */
    @RequestMapping("/getWarnData")
    public Result<List<WarnData>> getWarnData(@RequestParam("token") String token) {
        String openid = JWTutil.getClaimByToken(token).get("openid", String.class);
        return warnDataService.getWarnData(openid);
    }
}
