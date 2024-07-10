package com.wuwei.wuwei.demos.vo;

import lombok.Data;

@Data
public class Result<E> {
    E data;
    int code;

    public Result(E data, int code) {
        this.data = data;
        this.code = code;
    }
}
