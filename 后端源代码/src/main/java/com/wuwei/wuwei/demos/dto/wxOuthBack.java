package com.wuwei.wuwei.demos.dto;

import lombok.Data;

/**
 * {"session_key":"\/IkZjlZpIUAFICUVPmWbmw==","openid":"odoiU5CcaqZL3u32_2YELtHaA1pY"}
 *
 * {"errcode":41008,"errmsg":"missing code, rid: 65d56510-389d87bb-729c2f46"}
 *
 *
 */
@Data
public class wxOuthBack {
    public String session_key;
    public String openid;

    public  int errcode;

    public String errmsg;


    @Override
    public String toString() {
        return "wxOuthBack{" +
                "session_key='" + session_key + '\'' +
                ", openid='" + openid + '\'' +
                ", errcode=" + errcode +
                ", errmsg='" + errmsg + '\'' +
                '}';
    }
}
