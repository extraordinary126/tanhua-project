package com.yuhao.dubbo.api;

import com.yuhao.bean.Mongo.Comment;
import com.yuhao.enums.CommentType;

import java.util.List;

public interface CommentApi {
    //发布评论并获取数量
    Integer saveComment(Comment comment1);

    //分页查询
    List<Comment> getAllCommentsById(String movementId, CommentType commentType, Integer page, Integer pagesize);

    //判断comment数据是否存在  (是否点赞 喜欢)
    Boolean isLiked(String monmentId, Long userId, CommentType like);

    //删除comment
    Integer deleteComment(Comment comment);

    //给动态评论点赞
    Integer commentLike(String commentId);

    Integer commentDislike(String commentId);

    //查询谁给我点赞 评论 喜欢了 传入我的id  ,查询谁和我互动了
    List<Comment> getCommentsList(Long publishUserId, Integer page, Integer pagesize, CommentType commentType);
}
