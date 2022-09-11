package com.yuhao.api;

import com.yuhao.bean.Mongo.UserLike;
import com.yuhao.dubbo.api.UserLikeApi;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@DubboService
public class UserLikeApiImpl implements UserLikeApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Boolean saveOrUpdateLike(Long currentUserId, Long likeUserId, Boolean isLike) {
        try {


            Query query = Query.query(Criteria.where("userId").is(currentUserId).
                    and("likeUserId").is(likeUserId));
            UserLike userLike = mongoTemplate.findOne(query, UserLike.class);
            if (userLike != null) {
                //查到了数据 修改字段
                userLike.setIsLike(isLike);
                userLike.setUpdated(System.currentTimeMillis());
                mongoTemplate.save(userLike);
//            Update update = Update.update("isLike",isLike).set("updated",System.currentTimeMillis());
//            mongoTemplate.updateFirst(query, update, UserLike.class);
            } else {
                //没有查出数据 插入
                userLike = new UserLike(null, currentUserId, likeUserId,
                        true, System.currentTimeMillis(), System.currentTimeMillis());
                mongoTemplate.save(userLike);
            }
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Map<String, Integer> getLikeCount(Long currentUserId) {
        Query loveQuery = Query.query(Criteria.where("userId").is(currentUserId).and("isLike").is(true));
        //long loveCount = mongoTemplate.count(loveQuery, UserLike.class);
        List<UserLike> loveList = mongoTemplate.find(loveQuery, UserLike.class);
        Integer loveCount = loveList.size();

        Query fanQuery = Query.query(Criteria.where("likeUserId").is(currentUserId).and("isLike").is(true));
        //long fanCount = mongoTemplate.count(fanQuery, UserLike.class);
        List<UserLike> fanList = mongoTemplate.find(fanQuery, UserLike.class);
        Integer fanCount = fanList.size();

        Integer eachLoveCount = 0;
        for (int i = 0; i < loveList.size(); i++) {
            Long UserIdInLove = loveList.get(i).getUserId();
            Long likeUserIdInLove = loveList.get(i).getLikeUserId();
            Boolean isLikeInLove = loveList.get(i).getIsLike();
            for (int j = 0; j < fanList.size(); j++) {
                Long UserIdInFan = fanList.get(j).getUserId();
                Long likeUserIdInFan = fanList.get(j).getLikeUserId();
                Boolean isLikeInFan = fanList.get(j).getIsLike();
                //如果在两个集合中取出的 userId 和 likeId  两两有记录  且 两两喜欢
                if (UserIdInLove.equals(likeUserIdInFan) && likeUserIdInLove.equals(UserIdInFan) && (isLikeInFan && isLikeInLove)){
                    eachLoveCount++;
                }
            }
        }
        Map<String, Integer> map = new HashMap<>();
        map.put("loveCount", loveCount);
        map.put("fanCount", fanCount);
        map.put("eachLoveCount", eachLoveCount);
        return map;
    }
}
