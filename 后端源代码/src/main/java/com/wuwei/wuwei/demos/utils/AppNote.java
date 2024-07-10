package com.wuwei.wuwei.demos.utils;

import com.wuwei.wuwei.demos.entity.Device;
import com.wuwei.wuwei.demos.entity.DeviceType;
import com.wuwei.wuwei.demos.mapper.DeviceMapper;
import com.wuwei.wuwei.demos.mapper.PortableDeviceMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.Set;

@Component
@Slf4j(topic="AppNote")
public class AppNote {

    @Autowired
    DeviceMapper deviceMapper;

    @Autowired
    PortableDeviceMapper portableDeviceMapper;

    static class Aemo extends AbstractProcessor {

        @Override
        public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
            return false;
        }
    }

    /**
     * 给指定deviceId的设备用户发通知
     *
     * @param title
     * @param content
     * @param deviceId
     * @return
     */

    public void receive(String title, String content, String deviceId, DeviceType deviceType) {
        OkHttpClient okHttpClient = new OkHttpClient();
        if (deviceType == DeviceType.Portable) {
            //如果是移动设备报警
            String longitude;
            String latitude;
            Device portableDevice = portableDeviceMapper.getDevice(deviceId);
            latitude = portableDevice.latitude;
            longitude = portableDevice.longitude;

            //给第一个手机发通知
            Request req = new Request.Builder()
//                .url("https://uri.amap.com/marker?position="+longitude+","+latitude+"&name=警报所在的位置名称")

                    .url("https://api.day.app/VMcLLrokLQQ23YeEnpM62H/" + title + "/" + content + "/?sound=alarm" +
                            "&icon=https://pic.imgdb.cn/item/65ebc7239f345e8d0316a62e.png" +
                            "&url=https://api.day.app/VMcLLrokLQQ23YeEnpM62H/title/content/?url=https://uri.amap.com/marker?position=" + longitude + "," + latitude)
                    .get()
                    .build();
            Call call = okHttpClient.newCall(req);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    log.warn("Okhttp fail");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    response.close();
                    log.info("警报发送成功");
                }
            });


            //给第二个手机发通知
//            Request req2 = new Request.Builder()
////                .url("https://uri.amap.com/marker?position="+longitude+","+latitude+"&name=警报所在的位置名称")
//
//                    .url("https://api.day.app/trpsiZhAczmjpGqAbzbEuE/" + title + "/" + content + "/?sound=alarm" +
//                            "&icon=https://pic.imgdb.cn/item/65ebc7239f345e8d0316a62e.png" +
//                            "&url=https://api.day.app/trpsiZhAczmjpGqAbzbEuE/title/content/?url=https://uri.amap.com/marker?position=" + longitude + "," + latitude)
//                    .get()
//                    .build();
//            Call call2 = okHttpClient.newCall(req2);
//            call2.enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    System.out.println("Okhttp fail");
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    response.close();
//                    System.out.println("警报发送成功");
//                }
//            });


        } else if (deviceType == DeviceType.Device) {
            //如果是固定设备报警
            //给第一个手机发通知
//            Request req = new Request.Builder()
////                .url("https://uri.amap.com/marker?position="+longitude+","+latitude+"&name=警报所在的位置名称")
//                    .url("https://api.day.app/trpsiZhAczmjpGqAbzbEuE/" + title + "/" + content + "/?sound=alarm" +
//                            "&icon=https://pic.imgdb.cn/item/65ebc7239f345e8d0316a62e.png")
//                    .get()
//                    .build();
//            Call call = okHttpClient.newCall(req);
//            call.enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    System.out.println("Okhttp fail");
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) {
//                    response.close();
//                    System.out.println("警报发送成功");
//                }
//            });


            //给第二个手机发通知
            Request req2 = new Request.Builder()
//                .url("https://uri.amap.com/marker?position="+longitude+","+latitude+"&name=警报所在的位置名称")
                    .url("https://api.day.app/VMcLLrokLQQ23YeEnpM62H/" + title + "/" + content + "/?sound=alarm" +
                            "&icon=https://pic.imgdb.cn/item/65ebc7239f345e8d0316a62e.png")
                    .get()
                    .build();
            Call call2 = okHttpClient.newCall(req2);
            call2.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    log.warn("Okhttp fail");
                }

                @Override
                public void onResponse(Call call, Response response) {
                    response.close();
                    log.info("警报发送成功");
                }
            });


        }

    }

}
