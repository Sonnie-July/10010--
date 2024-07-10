package com.wuwei.wuwei.demos.mapper;

import com.wuwei.wuwei.demos.entity.WarnData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WarnDataMapper {

    public int addWarnData(@Param("name")String name,
                           @Param("tag")String tag,
                           @Param("openid")String openid,
                           @Param("address")String address,
                           @Param("date") String date,
                           @Param("deviceType") String deviceType
    );

    List<WarnData> getWarnData(@Param("openid") String deviceId);
}
