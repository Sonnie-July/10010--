package com.wuwei.wuwei.demos.mapper;

import com.wuwei.wuwei.demos.entity.Device;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface PortableDeviceMapper {

    int addDevice(@Param("deviceId") String deviceId,
                  @Param("openid") String openid,
                  @Param("name") String name,
                  @Param("x1") String x1,
                  @Param("x2") String x2,
                  @Param("y1") String y1,
                  @Param("y2") String y2);
    List<Device> getDevices(@Param("openid") String openid);

    Device getDevice(@Param("deviceId") String deviceId);
    int deleteDevice(@Param("openid") String openid ,@Param("deviceId") String deviceId);

    int updatelongitude(@Param("deviceId")String deviceId,@Param("longitude") String longitude);

    int updatelatitude(@Param("deviceId")String deviceId,@Param("latitude") String latitude);


    void PortableBulkdeletion(@Param("deleteIds") ArrayList deleteList,@Param("openid") String openid);
}
