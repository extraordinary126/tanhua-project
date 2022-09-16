package com.yuhao.api;

import com.yuhao.VO.PageResult;
import com.yuhao.bean.Mongo.Movement;
import com.yuhao.bean.Mongo.MovementTimeLine;
import com.yuhao.dubbo.api.MomentApi;
import com.yuhao.utils.IdWorker;
import com.yuhao.utils.TimeLineService;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.List;

@DubboService
public class MomentApiImpl implements MomentApi {

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    IdWorker idWorker;

    @Autowired
    TimeLineService timeLineService;

    //发布动态
    @Override
    public String sendMomoent(Movement movement) {
        try {
            //1.保存动态详情

            movement.setPid(idWorker.getNextId("movement"));
            movement.setCreated(System.currentTimeMillis());
            mongoTemplate.save(movement);
            //将代码抽取出一个工具类  加上Componet交给Spring管理 我们直接自动注入调用即可
            //在工具类上加入Async 执行时会建立一个新的线程执行方法
            //这样不需要等待保存结束 就可以返回保存成功！
            timeLineService.saveTimeLine(movement.getUserId(), movement.getId());
//            //2.查询当前用户的好友数据
//            Criteria criteria = Criteria.where("userId").is(movement.getUserId());
//            Query query = Query.query(criteria);
//            List<Friend> friends = mongoTemplate.find(query, Friend.class);
//            //3.循环好友数据,构建时间线数据放入好友的时间线表中
//            List<MovementTimeLine> timeLineList = new ArrayList<>();
//            for (Friend friend : friends) {
//                MovementTimeLine movementTimeLine = new MovementTimeLine();
//                movementTimeLine.setUserId(friend.getUserId());
//                movementTimeLine.setCreated(System.currentTimeMillis());
//                movementTimeLine.setMovementId(movement.getId());
//                movementTimeLine.setFriendId(friend.getFriendId());
//                mongoTemplate.save(movementTimeLine);
//            }
        }catch (Exception e){
            //进行手动的事务处理
            e.printStackTrace();
        }
        return movement.getId().toHexString();
    }

    @Override
    public PageResult getMoment(Long userId, Integer page, Integer pagesize) {
        Criteria criteria = Criteria.where("userId").is(userId).and("state").is(1);
        Query query = Query.query(criteria)
                .limit(pagesize)
                .skip((page - 1) * pagesize)
                .with(Sort.by(Sort.Order.desc("created"))); //发布时间排序
        List<Movement> movementsList = mongoTemplate.find(query, Movement.class);
        PageResult pageResult = new PageResult(page, pagesize, movementsList.size(), movementsList);
        return pageResult;
    }

    // 发布人是我的好友 我也是发布人的好友    所以我的id是朋友的friendId
    //查询当前用户好友所发布的动态 所以是根据传来的friendId 来在时间线表里查询发布人的
    @Override
    public List<Movement> getFriendMoment(Integer page, Integer pagesize, Long friendId) {
        //根据friendId查询时间线表
        Criteria criteria = Criteria.where("friendId").is(friendId).and("state").is(1);
        Query query = Query.query(criteria)
                .limit(pagesize)
                .skip((page - 1) * pagesize)
                .with(Sort.by(Sort.Order.desc("created")));
        List<MovementTimeLine> timeLineList = mongoTemplate.find(query, MovementTimeLine.class);
        //从时间线表中提取动态的id列表
       // List<ObjectId> list = CollUtil.getFieldValues(timeLineList, "movementId", ObjectId.class);
        List<ObjectId> list = new ArrayList<>();
        for (MovementTimeLine movementTimeLine : timeLineList){
            //查询动态的id 存入list
            ObjectId momentId = movementTimeLine.getMovementId();
            list.add(momentId);
        }
        ////根据动态id列表查询动态  查询id在list集合中的所有动态
        Criteria criteria1 = Criteria.where("id").in(list);
        Query query1 = Query.query(criteria1);
        List<Movement> movementList = mongoTemplate.find(query1, Movement.class);

        return movementList;
    }

    @Override
    public List<Movement> getMomentByPidsList(List<Long> pidsList) {
        Criteria criteria = Criteria.where("pid").in(pidsList);
        Query query = Query.query(criteria);
        return mongoTemplate.find(query, Movement.class);
    }

    //构建 counts 条随机动态 随机的pid动态
    @Override
    public List<Movement> randomMovements(Integer counts) {
        //1.创建统计对象,设置统计参数                         //这个class为了获取数据库表的参数
        TypedAggregation aggregation = Aggregation.newAggregation(Movement.class, Aggregation.sample(counts));
        //2.调用mongoTemplate的aggrate统计方法
        AggregationResults<Movement> results = mongoTemplate.aggregate(aggregation, Movement.class);//这个Movement.class 是表明结果构建成什么样的对象
        //3.获取统计结果
        List<Movement> movementList = results.getMappedResults();
        return movementList;
    }

    //根据动态id查询单挑动态
    @Override
    public Movement getSingleMoment(String id) {
        Movement movement = mongoTemplate.findById(id, Movement.class);
        return movement;
    }

    //根据id  和 state查询动态
    @Override
    public PageResult getMomentByIdAndState(Long userId, Integer state, Integer page, Integer pagesize) {
        Query query = new Query();
        if (userId != null){
            query.addCriteria(Criteria.where("userId").is(userId));
        }
        if (state != null){
            query.addCriteria(Criteria.where("state").is(state));
        }
//        Criteria criteria = null;
//        if (userId != null){
//             criteria = Criteria.where("userId").is(userId);
//        }
//        if (state != null){
//            if (userId != null) {
//                criteria = criteria.and("state").is(state);
//            }else {
//                criteria = Criteria.where("state").is(state);
//            }
//        }
//        if (userId == null && state == null){
//            criteria = new Criteria();
//        }
        long count = mongoTemplate.count(query, Movement.class);
        query.limit(pagesize).skip((page - 1) * pagesize)
                .with(Sort.by(Sort.Order.desc("created")));
        List<Movement> movementList = mongoTemplate.find(query, Movement.class);
        return new PageResult(page, pagesize, Math.toIntExact(count), movementList);
    }

    @Override
    public void update(String movementId, Integer state) {
        Query query = Query.query(Criteria.where("id").is(new ObjectId(movementId)));
        Update update = Update.update("state",state);
        mongoTemplate.updateFirst(query, update, Movement.class);
    };
}
