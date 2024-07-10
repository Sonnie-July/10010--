package com.wuwei.wuwei.demos.mapper;

import com.wuwei.wuwei.demos.entity.Device;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface DeviceMapper {

    ArrayList<Device> getDevices(@Param("openid") String openid);

    Device getDevice(@Param("deviceId") String deviceId);

    ArrayList<Device> getPortableDevices(@Param("openid") String openid);
    int addDevice(@Param("deviceId") String deviceId,
                  @Param("openid") String openid, @Param("name") String name,
                  @Param("pos") String pos, @Param("video") String video);



    int deleteDevice(@Param("openid") String openid, @Param("deviceId") String deviceId);

    int updateDevice(@Param("deviceId") String deviceId,@Param("name")String name,@Param("pos") String pos);


    void Bulkdeletion(@Param("deleteIds") List<String> deleteIds,@Param("openid") String openid);
}
