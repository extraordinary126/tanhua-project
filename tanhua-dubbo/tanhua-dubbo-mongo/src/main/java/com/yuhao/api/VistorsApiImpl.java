package com.yuhao.api;

import com.yuhao.bean.Mongo.Visitors;
import com.yuhao.dubbo.api.VistorsApi;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@DubboService
public class VistorsApiImpl implements VistorsApi {

    @Autowired
    private MongoTemplate mongoTemplate;


    //保存访客数据 但同一个用户的访问 一天只保存一次
    @Override
    public void save(Visitors visitors) {
        Query query = Query.query(Criteria.where("userId").is(visitors.getUserId())
                .and("visitorUserId").is(visitors.getVisitorUserId())
                .and("date").is(visitors.getDate()));
        if (!mongoTemplate.exists(query, Visitors.class)){
            mongoTemplate.save(visitors);
        }
    }

    //查询访客列表 (日期之后的)
    @Override
    public List<Visitors> getVisitorsList(Long date, Long userId) {

        Criteria criteria = Criteria.where("userId").is(userId);
        if (date != null){
            criteria = criteria.and("date").gt(date);
        }
        //只显示5条 按照访问时间倒序排序
        Query query = Query.query(criteria).limit(5).with(Sort.by(Sort.Order.desc("date")));
        List<Visitors> visitorsList = mongoTemplate.find(query, Visitors.class);
        return visitorsList;
    }


}
