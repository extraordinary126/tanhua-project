package com.yuhao.utils;

import com.yuhao.bean.Mongo.Friend;
import com.yuhao.bean.Mongo.MovementTimeLine;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TimeLineService {

    @Autowired
    MongoTemplate mongoTemplate;

    @Async
    public void saveTimeLine(Long userId, ObjectId movementId) {
        //2.查询当前用户的好友数据
        Criteria criteria = Criteria.where("userId").is(userId);
        Query query = Query.query(criteria);
        List<Friend> friends = mongoTemplate.find(query, Friend.class);
        //3.循环好友数据,构建时间线数据放入好友的时间线表中
        List<MovementTimeLine> timeLineList = new ArrayList<>();
        for (Friend friend : friends) {
            MovementTimeLine movementTimeLine = new MovementTimeLine();
            movementTimeLine.setUserId(friend.getUserId());
            movementTimeLine.setCreated(System.currentTimeMillis());
            movementTimeLine.setMovementId(movementId);
            movementTimeLine.setFriendId(friend.getFriendId());
            mongoTemplate.save(movementTimeLine);
        }
    }
}
