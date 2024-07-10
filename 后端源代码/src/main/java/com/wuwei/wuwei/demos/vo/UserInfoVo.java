package com.wuwei.wuwei.demos.vo;

import com.wuwei.wuwei.demos.entity.Device;
import lombok.Data;

import java.util.ArrayList;

@Data
public class UserInfoVo {

    ArrayList<Device> devices;
    String avatarUrl;
    String nickName;

    public UserInfoVo(ArrayList<Device> devices, String avatarUrl, String nickName) {
        this.devices = devices;
        this.avatarUrl = avatarUrl;
        this.nickName = nickName;
    }
}
