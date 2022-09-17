package com.tanhua.recommend.listener;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.yuhao.bean.Mongo.Movement;
import com.yuhao.bean.MovementScore;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RecommendMovementListener {

    @Autowired
    private MongoTemplate mongoTemplate;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    value = "tanhua.movement.queue",
                    durable = "true"
            ),
            exchange = @Exchange(
                    value = "tanhua.log.exchange",
                    type = ExchangeTypes.TOPIC),
            key = {"log.movement"})
    )
    public void recommend(String message) throws Exception {
        System.out.println("处理动态消息："+message);
        try {
            Map<String, Object> map = JSON.parseObject(message);
            //1、获取数据
            Long userId = Long.valueOf(map.get("userId").toString());
            String logTime = (String) map.get("logTime");
            String movementId = (String) map.get("busId");
            String type = (String) map.get("type");
            Movement movement = mongoTemplate.findById(movementId, Movement.class);
            if(movement != null) {
                MovementScore ms = new MovementScore();
                ms.setUserId(userId);
                ms.setDate(System.currentTimeMillis());
                ms.setMovementId(movement.getPid());
                ms.setScore(getScore(type,movement));
                mongoTemplate.save(ms);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Double getScore(String type,Movement movement) {
        //0201为发动态  基础5分 50以内1分，50~100之间2分，100以上3分
        //0202为浏览动态， 1
        //0203为动态点赞， 5
        //0204为动态喜欢， 8
        //0205为评论，     10
        //0206为动态取消点赞， -5
        //0207为动态取消喜欢   -8
        Double score = 0d;
        switch (type) {
            case "0201":
                score = 5d;
                score += movement.getMedias().size();
                int length = StrUtil.length(movement.getTextContent());
                if (length >= 0 && length < 50) {
                    score += 1;
                } else if (length < 100) {
                    score += 2;
                } else {
                    score += 3;
                }
                break;
            case "0202":
                score = 1d;
                break;
            case "0203":
                score = 5d;
                break;
            case "0204":
                score = 8d;
                break;
            case "0205":
                score = 10d;
                break;
            case "0206":
                score = -5d;
                break;
            case "0207":
                score = -8d;
                break;
            default:
                break;
        }
        return score;
    }
}

