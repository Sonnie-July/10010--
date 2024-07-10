package com.wuwei.wuwei.demos.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class GpsTransform {

    private static String tencentMapKey;

    @Value("${tencentMap.Key}")
    public void setTencentMapKey(String mapKey){
        tencentMapKey=mapKey;
    }

    public static String[] gpsTransformHandler(String[] gpsCoordinate) throws IOException {
        String[] coordinate = new String[2];
        //返回结果,0是经度,1是纬度
        OkHttpClient okHttpClient = new OkHttpClient();
        Request req = new Request.Builder()
//                .url("https://uri.amap.com/marker?position="+longitude+","+latitude+"&name=警报所在的位置名称")

                .url("https://apis.map.qq.com/ws/coord/v1/translate?key=" + tencentMapKey + "&locations="
                        + gpsCoordinate[0] + "," + gpsCoordinate[1] + "&type=1"
                )
                .get()
                .build();
        Response response = okHttpClient.newCall(req).execute();
        String responseData = null;
        if (response.body() != null) {
            responseData = response.body().string();
            response.close();
        }


        //解析腾讯地图转换api返回的Json数据
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(String.valueOf(responseData));
        JsonNode locations = root.at("/locations");
        locations.forEach(e -> {
            if (e.has("lng")) {
                coordinate[1] = e.get("lng").asText();
            }
            if (e.has("lat")) {
                coordinate[0] = e.get("lat").asText();
            }
        });

        return coordinate;
    }


}
