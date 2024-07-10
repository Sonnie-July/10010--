package com.wuwei.wuwei.demos.services;

import com.wuwei.wuwei.demos.entity.Device;
import com.wuwei.wuwei.demos.entity.DeviceType;
import com.wuwei.wuwei.demos.entity.WarnData;
import com.wuwei.wuwei.demos.mapper.DeviceMapper;
import com.wuwei.wuwei.demos.mapper.PortableDeviceMapper;
import com.wuwei.wuwei.demos.mapper.WarnDataMapper;
import com.wuwei.wuwei.demos.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class WarnDataService {
    @Autowired
    DeviceMapper deviceMapper;

    @Autowired
    PortableDeviceMapper portableDeviceMapper;

    @Autowired
    WarnDataMapper  warnDataMapper;




    public void pushWarnData(String deviceId, String tag, DeviceType deviceType){
        Device device;
        if (deviceType == DeviceType.Device){
             device = deviceMapper.getDevice(deviceId);
            String name = device.getName();
            String openid =device.getOpenid();
            String address = device.getPos();
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            warnDataMapper.addWarnData(name, tag,openid,address,formatter.format(date), String.valueOf(deviceType));
        }else {
            device = portableDeviceMapper.getDevice(deviceId);
            String name = device.getName();
            String openid =device.getOpenid();
            String address = "移动目标";
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            warnDataMapper.addWarnData(name, tag,openid,address,formatter.format(date), String.valueOf(deviceType));
        }

    }



    public Result<List<WarnData>> getWarnData(String deviceId){
        try {
            List<WarnData> warnData = warnDataMapper.getWarnData(deviceId);
            return new Result<>(warnData,200);
        }catch (Exception e){
            return new Result<>(null,500);
        }

    }



}
