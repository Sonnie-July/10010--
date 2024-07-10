package com.wuwei.wuwei.demos.controller;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wuwei.wuwei.demos.entity.DeviceType;
import com.wuwei.wuwei.demos.mapper.WarnDataMapper;
import com.wuwei.wuwei.demos.services.WarnDataService;
import com.wuwei.wuwei.demos.utils.AppNote;
import com.wuwei.wuwei.demos.utils.AsyncThread;
import com.wuwei.wuwei.demos.vo.Result;
import com.wuwei.wuwei.demos.entity.Device;
import com.wuwei.wuwei.demos.mapper.DeviceMapper;
import com.wuwei.wuwei.demos.utils.JWTutil;
import com.wuwei.wuwei.demos.vo.DeviceData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 用于固定设备
 */
@RestController
@Slf4j
public class DevicesController {

    @Autowired
    DeviceMapper deviceMapper;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    AppNote note;

    @Autowired
    WarnDataService warnDataService;

    @RequestMapping("/getDevices")
    public List<Device> getDevices(@RequestParam("token") String token) {
        String openid = JWTutil.getClaimByToken(token).get("openid", String.class);
        System.out.println("有用户发起了设备查询:" + openid);
        return deviceMapper.getDevices(openid);
    }


    @RequestMapping("/addDevice")
    public Result<String> addDevice(@RequestParam("deviceId") String deviceId,
                                    @RequestParam("token") String token, @RequestParam("name") String name,
                                    @RequestParam("pos") String pos, @RequestParam("video") String video) {
        try {
            String openid = JWTutil.getClaimByToken(token).get("openid", String.class);
            deviceMapper.addDevice(deviceId, openid, name, pos, video);
            return new Result<>("add success", 200);
        } catch (Exception e) {
            return new Result<>("add wrong", 500);
        }

    }


    @RequestMapping("/deleteDevice")
    public Result<String> deleteDevice(@RequestParam("token") String token, @RequestParam("deviceId") String deviceId) {
        try {
            String openid = JWTutil.getClaimByToken(token).get("openid", String.class);
            deviceMapper.deleteDevice(openid, deviceId);
            return new Result<>("delete success", 200);
        } catch (Exception e) {
            return new Result<>("delete wrong", 500);
        }

    }

    /**
     * 该接口用于修改简单的设备信息:名称,地址
     * 更新设备信息
     *
     * @return
     */
    @RequestMapping("/updateDevice")
    public Result<String> updateDevice(@RequestParam("deviceId") String deviceId,
                                       @RequestParam("token") String token, @RequestParam("name") String name,
                                       @RequestParam("pos") String pos) {
        try {
            String openid = JWTutil.getClaimByToken(token).get("openid", String.class);
            Device device = deviceMapper.getDevice(deviceId);
            if (Objects.equals(device.getOpenid(), openid)) {
                deviceMapper.updateDevice(deviceId, name, pos);
                return new Result<>("success", 200);
            } else {
                return new Result<>("Wrong token", 400);
            }
        } catch (Exception e) {
            return new Result<>(e.toString(), 500);
        }


    }

    /**
     * 该接口用于对接华为云,不参与返回前端
     * 结果是将查询的设备数据存在缓存中
     *
     * @param request
     * @return
     */
    @RequestMapping("/updateDeviceData")
    public Result<String> updateDeviceData(@RequestBody String request) {
        try {
            System.out.println(request);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(request);

            log.info("收到设备上传数据,开始解析");
            JsonNode notifyData = root.get("notify_data");
            String deviceId = notifyData.get("header").get("device_id").asText();
            JsonNode services = root.at("/notify_data/body/services");
            for (JsonNode service : services) {
                JsonNode properties = service.get("properties");

                if (properties.has("temperature")) {
                    String s = redisTemplate.opsForValue().get(deviceId + "_temperature");
                    int temperature = properties.get("temperature").asInt();
                    if (temperature>=32&&s==null){
                        note.receive("温度异常", "点击查看详情", deviceId,DeviceType.Device);
                        warnDataService.pushWarnData(deviceId,"温度异常", DeviceType.Device);
                    }
                    redisTemplate.opsForValue().set(deviceId + "_" + "temperature", String.valueOf(temperature),20,TimeUnit.SECONDS);


                }
                if (properties.has("humidity")) {
                    int humidity = properties.get("humidity").asInt();
                    redisTemplate.opsForValue().set(deviceId + "_" + "humidity", String.valueOf(humidity));

                }
                if (properties.has("mq2")) {
                    int mq2 = properties.get("mq2").asInt();
                    String s = redisTemplate.opsForValue().get(deviceId + "_air");
                    if (s == null&&mq2>1000) {
                        //防止频繁发请求
                        note.receive("有害气体警报", "点击查看详情", deviceId,DeviceType.Device);
                       warnDataService.pushWarnData(deviceId,"有害气体", DeviceType.Device);
                        //提交报警记录
                        redisTemplate.opsForValue().set(deviceId + "_air", "1", 40, TimeUnit.SECONDS);
                        //上锁
                    }
                    redisTemplate.opsForValue().set(deviceId + "_mq2", String.valueOf(mq2),20,TimeUnit.SECONDS);
                    //将数据保存到mq2中
                }

                if (properties.has("status")){
                    String s = redisTemplate.opsForValue().get(deviceId + "_status");
                    int status = properties.get("status").asInt();
                    if (status==1&&s==null){
                        //如果没有频繁发送
                        note.receive("老人疑似摔倒", "点击查看详情", deviceId,DeviceType.Device);
                        warnDataService.pushWarnData(deviceId,"摔倒", DeviceType.Device);
                        redisTemplate.opsForValue().set(deviceId + "_status", "1", 40, TimeUnit.SECONDS);
                    }

                }

                log.info("解析完成,存储完成");
            }
            return new Result<>("success", 200);
        } catch (Exception e) {
            return new Result<>(e.toString(), 500);
        }

    }


    @RequestMapping("/getDeviceData")
    public Result getDeviceData(@RequestParam("deviceId") String deviceId) {
        try {
            String temperature = redisTemplate.opsForValue().get(deviceId + "_temperature");
            String humidity = redisTemplate.opsForValue().get(deviceId + "_humidity");
            String mq2 = redisTemplate.opsForValue().get(deviceId + "_mq2");
            String status = redisTemplate.opsForValue().get(deviceId+"_status");
            return new Result<>(new DeviceData(temperature, humidity, mq2,1), 200);
//            if (status != null) {
//                return new Result<>(new DeviceData(temperature, humidity, mq2,Integer.parseInt(status)), 200);
//            }else {
//                return new Result<>(null, 500);
//            }
        } catch (Exception e) {
            return new Result<>(e.toString(), 500);
        }

    }


    @RequestMapping("/Bulkdeletion")
    @Transactional
    public Result<String> bulkDeletion(@RequestParam("deleteArray") String deleteArray,
                                       @RequestParam("token") String token) {
        try {
            String openid = JWTutil.getClaimByToken(token).get("openid", String.class);
            ArrayList deleteList = JSON.parseObject(deleteArray, ArrayList.class);
            deviceMapper.Bulkdeletion(deleteList, openid);
            return new Result<>("批量删除成功", 200);
        } catch (Exception e) {
            return new Result<>("删除错误", 500);
        }

    }

}