package com.tanhua.admin.listener;

import com.alibaba.fastjson.JSON;
import com.tanhua.admin.mapper.LogMapper;
import com.yuhao.bean.Log;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

//RabbitMQ监听器
@Component
public class LogListener {

    @Resource
    private LogMapper logMapper;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    value = "tanhua.log.queue",
                    durable = "true"
            ),
            exchange = @Exchange(
                    value = "tanhua.log.exchange",
                    type = ExchangeTypes.TOPIC),
            key = {"log.*"})
    )
    public void log(String message){
        try {
            Map map = JSON.parseObject(message, Map.class);
            map.forEach((k,v) -> System.out.println(k + " : " + v));
            //解析map 获取数据
            Long userId = Long.valueOf(map.get("userId").toString());
            String type = (String) map.get("type");
            String logTime = (String) map.get("logTime");
            Object busId = map.get("busId");
            //构造log对象,存入数据库中
            Log log = new Log(userId, logTime, type);
            logMapper.insert(log);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
