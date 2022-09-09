package com.yuhao.api;

import com.yuhao.bean.Mongo.Friend;
import com.yuhao.dubbo.api.FriendApi;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@DubboService
public class FriendApiImpl implements FriendApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void addFriend(Long friendId, Long userId) {
        //双向的好友关系


        //判断好友关系是否存在
        Query query = Query.query(Criteria.where("userId").is(userId).and("friendId").is(friendId));
        boolean exists = mongoTemplate.exists(query, Friend.class);
        //判断好友关系是否存在
        if (!exists) {
            Friend friend2 = new Friend(null, userId, friendId, System.currentTimeMillis());

            //如果不存在则保存
            mongoTemplate.insert(friend2);
        }


        Query query1 = Query.query(Criteria.where("userId").is(friendId).and("friendId").is(userId));
        boolean exists1 = mongoTemplate.exists(query1, Friend.class);
        if (!exists1) {
            Friend friend1 = new Friend(null, friendId, userId, System.currentTimeMillis());
            mongoTemplate.insert(friend1);
        }
    }

    //查询好友 根据keyword
    @Override
    public List<Friend> queryFriends(Long userId, Integer page, Integer pagesize) {
            Query query = Query.query(Criteria.where("userId").is(userId))
                    .skip((page - 1) * pagesize)
                    .limit(pagesize)
                    .with(Sort.by(Sort.Order.desc("created")));
            List<Friend> friendList = mongoTemplate.find(query, Friend.class);
            return friendList;
    }
}
