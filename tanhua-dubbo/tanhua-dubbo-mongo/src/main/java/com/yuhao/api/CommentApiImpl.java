package com.yuhao.api;


import com.yuhao.bean.Mongo.Comment;
import com.yuhao.bean.Mongo.Movement;
import com.yuhao.dubbo.api.CommentApi;
import com.yuhao.enums.CommentType;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import javax.annotation.Resource;
import java.util.List;

@DubboService
public class CommentApiImpl implements CommentApi {

    @Resource
    MongoTemplate mongoTemplate;

    //保存Comment  包括了Comment里面的三种类型: 点赞 评论 喜欢
    @Override
    public Integer saveComment(Comment comment1) {
        //1.查询动态
        Movement movement = mongoTemplate.findById(comment1.getPublishId(), Movement.class);
        //2.向comment对象设置被评论人属性
        if (movement != null) {
            //将动态的id 设置为
            comment1.setPublishUserId(movement.getUserId());
        }
        //3.保存到数据库
        mongoTemplate.save(comment1);
        //4.更新动态表中对应的字段
        Criteria criteria = Criteria.where("id").is(comment1.getPublishId());
        Query query = Query.query(criteria);
        Update update = new Update();
        //根据点赞 评论 喜欢三种分别做不同的更新  数量 + 1     1表示 +1     -1 表示  -1
        if (comment1.getCommentType() == CommentType.LIKE.getType()) {
            update.inc("likeCount", 1);
        } else if (comment1.getCommentType() == CommentType.COMMENT.getType()) {
            update.inc("commentCount", 1);
        } else if (comment1.getCommentType() == CommentType.LOVE.getType()) {
            update.inc("loveCount", 1);
        }
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);    //获取更新后的最新参数
        Movement movement1 = mongoTemplate.findAndModify(query, update, options, Movement.class);

        //5.获取最新的评论/点赞/喜欢数量返回
        return movement1.statisCount(comment1.getCommentType());
        /*if (comment1.getCommentType() == CommentType.LIKE.getType()){
            return movement1.getLikeCount();
        }else if (comment1.getCommentType() == CommentType.COMMENT.getType()){
            return movement1.getCommentCount();
        }else if (comment1.getCommentType() == CommentType.LOVE.getType()){
            return movement1.getLoveCount();
        }
        return null;*/
    }

    //保存Comment  视频页面下的点赞
    @Override
    public Integer saveVideoComment(Comment comment1) {
        //1.查询动态
        ObjectId publishId = comment1.getPublishId();
        Query query1 = Query.query(Criteria.where("_id").is(publishId));
        Comment comment = mongoTemplate.findOne(query1, Comment.class);
        //2.向comment对象设置被评论人属性
        if (comment != null) {
            //将动态的id 设置为
            comment1.setPublishUserId(comment.getUserId());
        }
        //3.保存到数据库
        mongoTemplate.save(comment1);
        //4.更新动态表中对应的字段
        Criteria criteria = Criteria.where("id").is(comment1.getPublishId());
        Query query = Query.query(criteria);
        Update update = new Update();
        //根据点赞 评论 喜欢三种分别做不同的更新  数量 + 1     1表示 +1     -1 表示  -1
        if (comment1.getCommentType() == CommentType.LIKE.getType()) {
            update.inc("likeCount", 1);
        } else if (comment1.getCommentType() == CommentType.COMMENT.getType()) {
            update.inc("commentCount", 1);
        }
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);    //获取更新后的最新参数
        Comment comment2 = mongoTemplate.findAndModify(query, update, options, Comment.class);
        //5.获取最新的评论/点赞/喜欢数量返回
        if (comment1.getCommentType() == CommentType.LIKE.getType()){
            return comment2.getLikeCount();
        }
        return null;
    }

    ///删除comment 点赞 取消点赞
    @Override
    public Integer deleteVideoCommentLike(Comment comment) {
        //1.删除comment中的数据
        Criteria criteria = Criteria.where("userId").is(comment.getUserId()).and("publishId").is(comment.getPublishId())
                .and("commentType").is(comment.getCommentType());
        Query query = Query.query(criteria);
        mongoTemplate.remove(query, Comment.class);
        //2.修改动态表comment评论中点赞的数量   - 1
        Query query1 = Query.query(Criteria.where("id").is(comment.getPublishId()));
        Update update = new Update();
        //根据点赞 评论 喜欢三种分别做不同的更新  数量 - 1
        if (comment.getCommentType() == CommentType.LIKE.getType()) {
            update.inc("likeCount", -1);
        }
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);    //获取更新后的最新参数
        Comment comment1 = mongoTemplate.findAndModify(query1, update, options, Comment.class);
        //5.获取最新的评论/点赞/喜欢数量返回
        return comment1.getCommentType();  //statisCount()就相当于上面的三个if
    }

    //根据Id查询所有评论
    @Override
    public List<Comment> getAllCommentsById(String movementId, CommentType commentType, Integer page, Integer pagesize) {
        //类型必须一致 转换成ObjectId类型
        Query query = Query.query(Criteria.where("publishId").is(new ObjectId(movementId)).and("commentType").is(commentType.getType()))
                .limit(pagesize)
                .skip((page - 1) * pagesize)
                .with(Sort.by(Sort.Order.desc("created")));
        List<Comment> comments = mongoTemplate.find(query, Comment.class);
        return comments;
    }

    //判断comment数据是否存在
    @Override
    public Boolean isLiked(String monmentId, Long userId, CommentType commentType) {
        Criteria criteria = Criteria.where("userId").is(userId).and("publishId").is(new ObjectId(monmentId))
                .and("commentType").is(commentType.getType());
        Query query = Query.query(criteria);
        boolean exists = mongoTemplate.exists(query, Comment.class); //判断数据是否存在
        return exists;
    }

    ///删除comment
    @Override
    public Integer deleteComment(Comment comment) {
        //1.删除comment中的数据
        Criteria criteria = Criteria.where("userId").is(comment.getUserId()).and("publishId").is(comment.getPublishId())
                .and("commentType").is(comment.getCommentType());
        Query query = Query.query(criteria);
        mongoTemplate.remove(query, Comment.class);
        //2.修改动态表movement中的数量   - 1

        Query query1 = Query.query(Criteria.where("id").is(comment.getPublishId()));
        Update update = new Update();
        //根据点赞 评论 喜欢三种分别做不同的更新  数量 - 1
        if (comment.getCommentType() == CommentType.LIKE.getType()) {
            update.inc("likeCount", -1);
        } else if (comment.getCommentType() == CommentType.COMMENT.getType()) {
            update.inc("commentCount", -1);
        } else if (comment.getCommentType() == CommentType.LOVE.getType()) {
            update.inc("loveCount", -1);
        }
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);    //获取更新后的最新参数
        Movement movement1 = mongoTemplate.findAndModify(query1, update, options, Movement.class);
        //5.获取最新的评论/点赞/喜欢数量返回
        return movement1.statisCount(comment.getCommentType());  //statisCount()就相当于上面的三个if
    }

    //给动态评论点赞
    @Override
    public Integer commentLike(String commentId) {
        Query query = Query.query(Criteria.where("id")
                .is(commentId).and("commentType")
                .is(CommentType.COMMENT.getType()));//查询条件
        Update update = new Update();
        update.inc("likeCount", 1); //修改的字段  + 1
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);    //获取更新后的最新参数
        Comment comment = mongoTemplate.findAndModify(query, update, options, Comment.class);
        if (comment != null) {
            return comment.getLikeCount();
        }
        return null;
    }

    //取消动态的点赞
    @Override
    public Integer commentDislike(String commentId) {
        Query query = Query.query(Criteria.where("id")
                .is(commentId).and("commentType")
                .is(CommentType.COMMENT.getType()));//查询条件
        Update update = new Update();
        update.inc("likeCount", -1); //修改的字段  + 1
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);    //获取更新后的最新参数
        Comment comment = mongoTemplate.findAndModify(query, update, options, Comment.class);
        if (comment != null) {
            return comment.getLikeCount();
        }
        return null;
    }

    //    //查询谁给我点赞 评论 喜欢了 传入我的id  ,查询谁和我互动了
    @Override
    public List<Comment> getCommentsList(Long publishUserId, Integer page, Integer pagesize, CommentType commentType) {
        Query query = Query.query(Criteria
                .where("publishUserId").is(publishUserId)
                .and("commentType").is(commentType.getType()))
                .limit(pagesize)
                .skip((page - 1) * pagesize)
                .with(Sort.by(Sort.Order.desc("created")));
        List<Comment> commentList = mongoTemplate.find(query, Comment.class);
        return commentList;
    }

    //视频下发布评论
    @Override
    public void publishVideoComment(Comment comment) {
        //要在video中 评论数+1   且在comment中插入评论内容
        comment.setCreated(System.currentTimeMillis());
        mongoTemplate.save(comment);
    }
}
