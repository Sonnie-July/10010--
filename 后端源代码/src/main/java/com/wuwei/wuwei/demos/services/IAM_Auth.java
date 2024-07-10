package com.wuwei.wuwei.demos.services;

import okhttp3.*;

import java.io.IOException;
import java.util.List;

/**
 * 没用了
 */
@Deprecated
public class IAM_Auth {

    public static String getToken() throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{ \n    \"auth\": { \n        \"identity\": { \n            \"methods\": [ \n                \"password\" \n            ], \n            \"password\": { \n                \"user\": { \n                    \"name\": \"ForAIoT\", \n                    \"password\": \"Zqt15083106556\", \n                    \"domain\": { \n                        \"name\": \"zhang_qintao\" \n                    } \n                } \n            } \n        }, \n        \"scope\": { \n            \"project\": { \n                \"name\": \"cn-north-4\" \n            } \n        } \n    } \n}");
        Request request = new Request.Builder()
                .url("https://iam.cn-north-4.myhuaweicloud.com/v3/auth/tokens")
                .method("POST", body)
                .addHeader("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "*/*")
                .addHeader("Host", "iam.cn-north-4.myhuaweicloud.com")
                .addHeader("Connection", "keep-alive")
                .build();
        Response response = client.newCall(request).execute();
        List<String> headers = response.headers("X-Subject-Token");
        return headers.toString();
    }

}
