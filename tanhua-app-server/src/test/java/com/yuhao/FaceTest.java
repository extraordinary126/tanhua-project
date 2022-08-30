package com.yuhao;

import com.baidu.aip.face.AipFace;
import org.json.JSONObject;

import java.util.HashMap;

public class FaceTest {
        //设置APPID/AK/SK
        public static final String APP_ID = "27223159";
        public static final String API_KEY = "uquLg0GObdN5P92R1xaNDCB8";
        public static final String SECRET_KEY = "p3ptWxdCplCjNH8GGHlBgiZlXsjRX9q8";

        public static void main(String[] args) {
            // 初始化一个AipFace
            AipFace client = new AipFace(APP_ID, API_KEY, SECRET_KEY);

            // 可选：设置网络连接参数
            client.setConnectionTimeoutInMillis(2000);
            client.setSocketTimeoutInMillis(60000);


            // 调用接口
            String image = "https://tanhuatanhua111.oss-cn-hangzhou.aliyuncs.com/2022/08/30/ba68008f-62db-4424-8632-faba24c2e50c.JPG";
            String imageType = "URL";

            HashMap<String, String> options = new HashMap<String, String>();
            options.put("face_field", "age");
            options.put("max_face_num", "2");
            options.put("face_type", "LIVE");
            options.put("liveness_control", "LOW");

            // 人脸检测
            JSONObject res = client.detect(image, imageType, options);
            System.out.println(res.toString(2));

        }

    }