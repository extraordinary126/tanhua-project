package com.yuhao.api;

import cn.hutool.core.collection.CollUtil;
import com.yuhao.VO.PageResult;
import com.yuhao.bean.Mongo.RecommendUser;
import com.yuhao.bean.Mongo.UserLike;
import com.yuhao.dubbo.api.RecommendUserApi;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@DubboService
public class RecommendUserApiImpl implements RecommendUserApi {

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    RecommendUserApi recommendUserApi;

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

    @Override
    public List<RecommendUser> queryCardsList(Long id, int counts) {
        //1. 排除已经喜欢 和 不喜欢的用户
        Query query = Query.query(Criteria.where("userId").is(id));
        List<UserLike> userLikeList = mongoTemplate.find(query, UserLike.class);
        //得到喜欢或不喜欢过的用户idList
        List<Long> userIdList = CollUtil.getFieldValues(userLikeList, "likeUserId", Long.class);
        //2.随机展示
        Criteria criteria = Criteria.where("toUserId").is(id).and("userId").nin(userIdList);
        //3.使用统计函数,随机获取推荐的用户列表
        TypedAggregation<RecommendUser> aggregation = Aggregation.newAggregation(RecommendUser.class,
                Aggregation.match(criteria),     //构造查询条件
                Aggregation.sample(counts)
        );
        AggregationResults<RecommendUser> results = mongoTemplate.aggregate(aggregation, RecommendUser.class);
        List<RecommendUser> mappedResults = results.getMappedResults();
        return mappedResults;
    }
}
