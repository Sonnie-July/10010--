package com.wuwei.wuwei.demos.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JWTutil {
    //过期时间
    private static final long expire = 24 * 60 * 60 * 30; // token的过期时间为30天（24小时*30）;
    private static final String secret = "123888";//自定义密钥，会根据这个密钥来加密签名

    //定义创建token的方法
    public static String getToken(String  openid) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 1000 * expire);

        return Jwts.builder()
                .setHeaderParam("type", "JWT")//类型，固定的
                .claim("openid", openid)//载荷
                .setIssuedAt(expiration)//过期时间
                .signWith(SignatureAlgorithm.HS512, secret)//签名算法
                .compact();//合成
    }

    //定义创建token的解析方法，将token解密成Claims类对象（Claims是jwt依赖自带的类，用于封装）
    public static Claims getClaimByToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }


    /**
     * 判断token是否过期,错误,等等一系列
     **/
    public static boolean JudgeTokenIsOK(String args) {
        Date now = new Date();
        try {
            Claims claims = getClaimByToken(args);
            if (claims.getIssuedAt().before(now))//如果token过期
            {
                return false;
            }
            /*可以对token继续加校验规则*/
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}

