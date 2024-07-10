package com.wuwei.wuwei.demos.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeviceData {
    String temperature;
    String humidity;
    String mq2;
    Integer status;


}
