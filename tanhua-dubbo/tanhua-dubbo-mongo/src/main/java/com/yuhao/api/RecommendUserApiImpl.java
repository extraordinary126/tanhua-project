package com.yuhao.api;

import com.yuhao.VO.PageResult;
import com.yuhao.bean.Mongo.RecommendUser;
import com.yuhao.dubbo.api.RecommendUserApi;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

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

    //mongodb 分页查询
    @Override
    public PageResult getRecommendUserList(Integer page, Integer pagesize, Long toUserId) {
        //1.构建 Criteria对象
        Criteria criteria = Criteria.where("toUserId").is(toUserId);
        //2.构建 Query对象
        Query query = Query.query(criteria);
//                .skip((page - 1) * pagesize)
//                .limit(pagesize);
        //3.查询总数
        long count = mongoTemplate.count(query, RecommendUser.class);
        //4.查询数据列表
         query.limit((page - 1) * pagesize)
                .skip(pagesize);
        List<RecommendUser> list = mongoTemplate.find(query, RecommendUser.class);
        //5.构造返回值
        return new PageResult(page, pagesize, (int) count ,list);
    }

    @Override
    public RecommendUser getTwoPeopleScore(Long userId, Long toUserId) {
        Query query = Query.query(Criteria.where("userId").is(userId).and("toUserId").is(toUserId));
        RecommendUser recommendUser = mongoTemplate.findOne(query, RecommendUser.class);
        if (recommendUser == null){
            //如果没有推荐的 那么随机建立一个推荐
            recommendUser = new RecommendUser();
            //推荐表构建两人id联系
            recommendUser.setUserId(userId);
            recommendUser.setToUserId(toUserId);
            //给一个缘分值
            recommendUser.setScore(90D);
        }
        return recommendUser;
    }
}
