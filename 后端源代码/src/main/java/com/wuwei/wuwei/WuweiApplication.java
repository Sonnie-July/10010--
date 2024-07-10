package com.wuwei.wuwei;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.IOException;
import java.nio.Buffer;

@SpringBootApplication
@EnableFeignClients
@EnableWebMvc
public class WuweiApplication {


    public static void main(String[] args) throws IOException {
        SpringApplication.run(WuweiApplication.class, args);
    }

}
