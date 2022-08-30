package com.yuhao;

import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.aliyun.teaopenapi.models.Config;

public class SendSms {

    /**
     * 使用AK&SK初始化账号Client
     * @param accessKeyId
     * @param accessKeySecret
     * @return Client
     * @throws Exception
     */
    public static com.aliyun.dysmsapi20170525.Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        Config config = new Config()
                // 您的AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 您的AccessKey Secret
                .setAccessKeySecret(accessKeySecret)
                .setEndpoint("dysmsapi.aliyuncs.com");
        // 访问的域名
        return new com.aliyun.dysmsapi20170525.Client(config);
    }

    public static void main(String[] args_) throws Exception {
        java.util.List<String> args = java.util.Arrays.asList(args_);
        com.aliyun.dysmsapi20170525.Client client = SendSms
                .createClient("LTAI5tQgnbNQvJ1mGv34qGNG", "LeMKlCtHzjEKqTJnBTqlDjj6lIhGxN");

        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setPhoneNumbers("15102903981") //目标手机号
                .setSignName("阿里云短信测试") //签名名称
                .setTemplateCode("SMS_154950909") //短信模板code
                .setTemplateParam("{\"code\":\"4396\"}"); //模板中变量替换
        SendSmsResponse sendSmsResponse = client.sendSms(sendSmsRequest);

        SendSmsResponseBody body = sendSmsResponse.getBody();

        // code = OK 代表成功
        System.out.println(body.getCode() + "  " + body.getMessage());
    }

}