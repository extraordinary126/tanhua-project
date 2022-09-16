package com.yuhao.api;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.yuhao.VO.PageResult;
import com.yuhao.bean.Mongo.FocusUser;
import com.yuhao.bean.Mongo.Video;
import com.yuhao.dubbo.api.VideoApi;
import com.yuhao.utils.IdWorker;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

@DubboService
public class VideoApiImpl implements VideoApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IdWorker idWorker;

    //保存视频对象  返回视频id
    @Override
    public String save(Video video) {
        video.setCreated(System.currentTimeMillis());
        video.setVid(idWorker.getNextId("video"));
        Video retVideo = mongoTemplate.save(video);
        return retVideo.getId().toHexString();
    }

    @Override
    public List<Video> findVideosByIds(List<Long> vids) {
        Query query = Query.query(Criteria.where("vid").in(vids));
        List<Video> list = mongoTemplate.find(query, Video.class);
        return list ;
    }

    @Override
    public List<Video> findVideosList(int page, Integer pagesize) {
        Query query = new Query().limit(pagesize).skip((page - 1) * pagesize)
                .with(Sort.by(Sort.Order.desc("created")));
        List<Video> list = mongoTemplate.find(query, Video.class);
        return list;
    }

    //关注视频作者
    @Override
    public Boolean userFocus(FocusUser focusUser) {
        if (focusUser != null) {
            focusUser.setCreated(System.currentTimeMillis());
            mongoTemplate.save(focusUser);
            return true;
        }
        return false;
    }

    //取关视频作者
    @Override
    public Boolean userUnFocus(Long currentUserId, Long videoUserId) {
        Query query = Query.query(Criteria.where("userId").is(currentUserId).and("followUserId").is(videoUserId));
        DeleteResult remove = mongoTemplate.remove(query, FocusUser.class);
        return remove.getDeletedCount() == 1;
    }

    @Override
    public Boolean videoLike(String id) {
        Query query = Query.query(Criteria.where("id").is(id));
        Update update = new Update();
        update.inc("likeCount", 1);
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Video.class);
        return updateResult.getModifiedCount() != 0;
    }

    @Override
    public Boolean videoDisLike(String id) {
        Query query = Query.query(Criteria.where("id").is(id));
        Update update = new Update();
        update.inc("likeCount", -1);
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Video.class);
        return updateResult.getModifiedCount() != 0;
    }


    //根据视频id查询视频发布者
    @Override
    public Long getVideoPublisher(String id) {
        Video video = mongoTemplate.findById(id, Video.class);
        if (video == null){
            return null;
        }
        return video.getUserId();
    }

    //评论数量+1
    @Override
    public Integer commentCountPlusOne(String id) {
        Update update = new Update();
        update.inc("commentCount", 1);
        Query query = Query.query(Criteria.where("id").is(id));
        Video video = mongoTemplate.findAndModify(query, update, Video.class);
        return video.getCommentCount();
    }

    //根据id查询用户视频列表
    @Override
    public PageResult getVideoListById(Integer page, Integer pagesize, Long userId) {
        Query query = Query.query(Criteria.where("userId").in(userId));
        long count = mongoTemplate.count(query, Video.class);
        query.limit(pagesize).skip((page - 1) * pagesize)
                .with(Sort.by(Sort.Order.desc("created")));
        List<Video> videoList = mongoTemplate.find(query, Video.class);
        return new PageResult(page, pagesize, Math.toIntExact(count), videoList);
    }
}
