package com.yuhao.api;

import com.yuhao.bean.Mongo.RecommendUser;
import com.yuhao.dubbo.api.RecommendUserApi;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@DubboService
public class RecommendUserApiImpl implements RecommendUserApi {

    @Autowired
    MongoTemplate mongoTemplate;

    //查询今日佳人
    @Override
    public RecommendUser queryWithMaxScore(Long toUserId) {
        //1.根据toUser来查询  查询出socre最高的
        //构建Criteria
        Criteria criteria = Criteria.where("toUserId").is(toUserId);
        //构建Query对象
        Query query = Query.query(criteria).with(Sort.by(Sort.Order.desc("score"))).limit(1);
        //调用MongoTemplate查询
        RecommendUser one = mongoTemplate.findOne(query, RecommendUser.class);
        return one;
    }
}
