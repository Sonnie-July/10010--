package com.wuwei.wuwei.demos.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Device {
    public String name;
    public String pos;
    public String deviceId;
    public String video;
    //存放视频流地址
    public String openid;
    public String longitude;
    public String status;
    public String latitude;
    public String x1;
    public String x2;
    public String y1;
    public String y2;
    public ArrayList<String> polygons;
}
