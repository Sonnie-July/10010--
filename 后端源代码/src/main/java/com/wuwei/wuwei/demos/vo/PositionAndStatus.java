package com.wuwei.wuwei.demos.vo;

import lombok.Data;

@Data
public class PositionAndStatus {
    String  position;
    String status;

    public PositionAndStatus(String position, String status) {
        this.position = position;
        this.status = status;
    }
}
