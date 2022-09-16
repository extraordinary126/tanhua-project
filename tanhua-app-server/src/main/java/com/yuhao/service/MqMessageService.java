package com.yuhao.service;

import com.alibaba.fastjson.JSON;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


//RabbitMQ消息发送工具类
@Service
public class MqMessageService {

    @Autowired
    private AmqpTemplate amqpTemplate;

    //发送日志消息

    /**
     * 操作类型,
     * 0101为登录
     * ，0201为发动态，0202为浏览动态，0203为动态点赞，0204为动态喜欢，0205为评论，0206为动态取消点赞，0207为动态取消喜欢
     * ，0301为发小视频，0302为小视频点赞，0303为小视频取消点赞，0304为小视频评论
     * @param userId  用户id
     * @param type    操作类型
     * @param key     要拼接的key
     * @param busId    业务id   比如动态id  视频id
     */
    public void sendLogMessage(Long userId,String type,String key,String busId) {
        try {
            Map map = new HashMap();
            map.put("userId",userId.toString());
            map.put("type",type);
            map.put("logTime",new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            map.put("busId",busId);     //业务id  (动态id  视频id)
            String message = JSON.toJSONString(map);
            amqpTemplate.convertAndSend("tanhua.log.exchange",
                    "log."+key,message);
        } catch (AmqpException e) {
            e.printStackTrace();
        }
    }

    //发送动态审核消息
    public void sendAudiMessage(String movementId) {
        try {
            amqpTemplate.convertAndSend("tanhua.audit.exchange",
                    "audit.movement",movementId);
        } catch (AmqpException e) {
            e.printStackTrace();
        }
    }
}