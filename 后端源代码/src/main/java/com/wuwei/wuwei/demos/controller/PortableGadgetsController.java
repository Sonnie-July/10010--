package com.wuwei.wuwei.demos.controller;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.wuwei.wuwei.demos.entity.Device;
import com.wuwei.wuwei.demos.entity.DeviceType;
import com.wuwei.wuwei.demos.mapper.PortableDeviceMapper;
import com.wuwei.wuwei.demos.services.WarnDataService;
import com.wuwei.wuwei.demos.utils.AppNote;
import com.wuwei.wuwei.demos.utils.AsyncThread;
import com.wuwei.wuwei.demos.utils.GpsTransform;
import com.wuwei.wuwei.demos.utils.JWTutil;
import com.wuwei.wuwei.demos.vo.Result;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
@Slf4j
public class PortableGadgetsController {

    @Autowired
    AppNote appNote;

    @Autowired
    PortableDeviceMapper portableDeviceMapper;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    WarnDataService warnDataService;


    /**
     * 添加移动设备
     * @param deviceId
     * @param token
     * @param name
     * @param x1
     * @param x2
     * @param y1
     * @param y2
     * @return
     */
    @RequestMapping("/addPortableDevice")
    public Result<String> addPortableDevice(@RequestParam("deviceId") String deviceId,
                                            @RequestParam("token") String token,
                                            @RequestParam("name") String name,
                                            @RequestParam("x1") String x1,
                                            @RequestParam("x2") String x2,
                                            @RequestParam("y1") String y1,
                                            @RequestParam("y2") String y2) {
        String openid = JWTutil.getClaimByToken(token).get("openid", String.class);
        portableDeviceMapper.addDevice(deviceId, openid, name, x1, x2, y1, y2);
        return new Result<>("添加完成", 200);
    }

    /**
     * 获取移动设备列表
     * @param token
     * @return
     */
    @RequestMapping("/getPortableDevice")
    public Result<List<Device>> getPortableDevice(@RequestParam("token") String token) {
        String openid = JWTutil.getClaimByToken(token).get("openid", String.class);
        List<Device> devices = portableDeviceMapper.getDevices(openid);

        devices.forEach((e) -> {
            e.polygons = new ArrayList<>();
            e.polygons.add(e.x1);
            e.polygons.add(e.x2);
            e.polygons.add(e.y1);
            e.polygons.add(e.y2);
        });
        return new Result<>(devices, 200);
    }

    /**
     * 删除某个设备
     * @param token
     * @param deviceId
     * @return
     */
    @RequestMapping("/deletePortableDevice")
    public Result<String> deleteDevice(@RequestParam("token") String token, @RequestParam("deviceId") String deviceId) {
        try {
            String openid = JWTutil.getClaimByToken(token).get("openid", String.class);
            portableDeviceMapper.deleteDevice(openid, deviceId);
            return new Result<>("delete success", 200);
        } catch (Exception e) {
            return new Result<>("delete wrong", 500);
        }

    }

    /**
     * 批量删除
     * @param deleteArray
     * @param token
     * @return Result
     */
    @RequestMapping("/PortableBulkdeletion")
    public Result<String> PortablebulkDeletion(@RequestParam("deleteArray") String deleteArray,
                                               @RequestParam("token") String token) {
        try {
            String openid = JWTutil.getClaimByToken(token).get("openid", String.class);
            ArrayList deleteList = JSON.parseObject(deleteArray, ArrayList.class);
            portableDeviceMapper.PortableBulkdeletion(deleteList, openid);
            return new Result<>("批量删除成功", 200);
        } catch (Exception e) {
            return new Result<>("删除错误", 500);
        }

    }

    /**
     * 该接口用于对接华为云,不参与返回前端
     * 结果是将查询的设备数据存在数据库中
     * @param  request String
     * @return Result<String>
     * @throws IOException
     */
    @RequestMapping("/updatePositionAndStatus")
    public Result<String> updatePositionAndStatus(@RequestBody String request) throws IOException, ExecutionException, InterruptedException, TimeoutException {
        System.out.println(request);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(request);


        JsonNode notifyData = root.get("notify_data");
        String deviceId = notifyData.get("header").get("device_id").asText();
        log.info("收到设备上传数据,开始解析,id为:" + deviceId);
        JsonNode services = root.at("/notify_data/body/services");
        try {
            for (JsonNode service : services) {
                JsonNode properties = service.get("properties");
                if (properties.has("latitude")) {
                    double latitude = properties.get("latitude").asDouble();
                    String[] transformed = GpsTransform.gpsTransformHandler(new String[]{String.valueOf(latitude/100), "116.33333"});
                    //转化经度
                    //只要第一个数据转换结果,第二个是凑数的
                    portableDeviceMapper.updatelatitude(deviceId, transformed[0]);
                }

                if (properties.has("longitude")) {
                    double  longitude = properties.get("longitude").asDouble();
                    String[] transformed = GpsTransform.gpsTransformHandler(new String[]{"116.33333",String.valueOf(longitude/100)});
                    //转换纬度
                    //只要第一个数据转换结果,第二个是凑数的
                    portableDeviceMapper.updatelongitude(deviceId, transformed[1]);
                }

                if (properties.has("status")) {
                    int status = properties.get("status").asInt();
                    String s = redisTemplate.opsForValue().get(deviceId + "_status");
                    if (status == 1&&s==null) {
                        //防止频繁推送通知,会在redis中设置一个会到期的"锁?"
                        appNote.receive("老人似乎摔倒", "点击查看老人位置", deviceId,DeviceType.Portable);
                        AsyncThread.submitTask(() -> warnDataService.pushWarnData(deviceId, "摔倒", DeviceType.Portable));
                        //提交报警记录
                        redisTemplate.opsForValue().set(deviceId + "_status", "1", 45, TimeUnit.SECONDS);
                    }

                }
                log.info("解析完成,存储完成");
            }

            return new Result<>("success", 200);
            //返回,这样物联网平台查看可以看出是否成功
        }catch (Exception e){
            return new Result<>("fail",500);
        }

    }

}
