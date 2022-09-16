package com.yuhao.service;

import cn.hutool.core.collection.CollUtil;
import com.yuhao.VO.CommentVo;
import com.yuhao.VO.ErrorResult;
import com.yuhao.VO.PageResult;
import com.yuhao.VO.VisitorsVo;
import com.yuhao.bean.Mongo.Comment;
import com.yuhao.bean.Mongo.Visitors;
import com.yuhao.bean.UserInfo;
import com.yuhao.common.utils.Constants;
import com.yuhao.dubbo.api.CommentApi;
import com.yuhao.dubbo.api.UserInfoApi;
import com.yuhao.dubbo.api.VistorsApi;
import com.yuhao.enums.CommentType;
import com.yuhao.exception.BuinessException;
import com.yuhao.interceptor.UserThreadLocalHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CommentsService {


    @DubboReference
    CommentApi commentApi;

    @DubboReference
    UserInfoApi userInfoApi;

    @DubboReference
    VistorsApi vistorsApi;

    @Autowired
    MqMessageService mqMessageService;

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    //提交评论              //动态ID      //评论内容
    public void comments(String movementId, String comment) {
        mqMessageService.sendLogMessage(UserThreadLocalHolder.getId(), "0205", "movement", movementId);
        //1.获取操作用户ID
        Long userId = UserThreadLocalHolder.getId();
        //2.构造Comment
        Comment comment1 = new Comment();
        comment1.setPublishId(new ObjectId(movementId));
        //设置类型:评论
        comment1.setCommentType(CommentType.COMMENT.getType());
        comment1.setContent(comment);
        comment1.setUserId(userId);
        comment1.setCreated(System.currentTimeMillis());
        //3.调用API保存评论
        Integer commentCount = commentApi.saveComment(comment1);
        //log.info("commentCount" + commentCount);
    }

    //查看评论
    public PageResult showComments(String movementId, Integer page, Integer pagesize) {
        //1.调用API查询评论列表
        List<Comment> list = commentApi.getAllCommentsById(movementId,CommentType.COMMENT, page, pagesize);
        //2.判断list集合是否存在
        if (list == null || list.size() == 0){
            return new PageResult();
        }
        //3.提取所有用户id 调用userinfoApi查询用户详情
        List<Long> userIdList = new ArrayList<>();
        for(Comment comment : list){
            userIdList.add(comment.getUserId());
        }
        Map<Long, UserInfo> userInfoMap = userInfoApi.getUserInfoMap(userIdList, null);
        //4.构造VO对象

        List<CommentVo> voList = new ArrayList<>();
        for(Comment comment : list){
            Long userId = comment.getUserId();
            UserInfo userInfo = userInfoMap.get(userId);
            CommentVo vo = CommentVo.init(userInfo, comment);
            voList.add(vo);
        }
        //5.构造返回值
        PageResult pageResult = new PageResult(page, pagesize, voList.size(), voList);
        return pageResult;
    }

    //给动态点赞
    public Integer like(String monmentId) {
        mqMessageService.sendLogMessage(UserThreadLocalHolder.getId(), "0203", "movement", monmentId);
        Long userId = UserThreadLocalHolder.getId();
        //1.查询用户是否已经点赞
        Boolean isLiked = commentApi.isLiked(monmentId, userId, CommentType.LIKE);
        //2.如果已经点赞 抛出异常
        if (isLiked){
            throw new BuinessException(ErrorResult.likeError());
        }
        //3.如果未点赞 调用API将数据保存到MongoDB
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(monmentId));
        comment.setCommentType(CommentType.LIKE.getType());
        comment.setUserId(userId);
        comment.setCreated(System.currentTimeMillis());
        Integer count = commentApi.saveComment(comment);
        //4.拼接RedisKey 将用户的点赞状态存入Redis中
        String key = Constants.MOVEMENTS_INTERACT_KEY + monmentId;
        String hashKey = Constants.MOVEMENT_LIKE_HASHKEY + userId;
        //key:动态id 相当于一个命名空间 空间就是这条动态
        //Redis的hash 是一个string类型的key和value的映射表，这里的value是一系列的键值对，hash特别适合用于存储对象。
        //哈希类型的数据操作总的思想是通过key和field操作value，key是数据标识，field是域，value是我们感
        //兴趣的业务数据。
        redisTemplate.opsForHash().put(key, hashKey , "1");

        return count;
    }

    //取消点赞
    public Integer dislike(String monmentId) {
        mqMessageService.sendLogMessage(UserThreadLocalHolder.getId(), "0206", "movement", monmentId);
        Long userId = UserThreadLocalHolder.getId();
        //1.调用api查看是否已经点赞
        Boolean isLiked = commentApi.isLiked(monmentId, userId, CommentType.LIKE);
        //2.如果未点赞 抛出异常
        if (!isLiked){
            throw new BuinessException(ErrorResult.disLikeError());
        }
        //3.如果点赞 调用api  删除数据 返回点赞数量
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(monmentId));
        comment.setCommentType(CommentType.LIKE.getType());
        comment.setUserId(userId);
        Integer count = commentApi.deleteComment(comment);
        //拼接redis 的key  删除点赞状态
        String key = Constants.MOVEMENTS_INTERACT_KEY + monmentId;
        String hashKey = Constants.MOVEMENT_LIKE_HASHKEY + userId;
        redisTemplate.opsForHash().delete(key, hashKey);

        return count;
    }

    public Integer love(String monmentId) {
        mqMessageService.sendLogMessage(UserThreadLocalHolder.getId(), "0204", "movement", monmentId);
        Long userId = UserThreadLocalHolder.getId();
        //1.查询用户是否已经点赞/喜欢
        Boolean isloved = commentApi.isLiked(monmentId, userId, CommentType.LOVE);
        //2.如果已经点赞 抛出异常
        if (isloved){
            throw new BuinessException(ErrorResult.loveError());
        }
        //3.如果未点赞 调用API将数据保存到MongoDB
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(monmentId));
        comment.setCommentType(CommentType.LOVE.getType());
        comment.setUserId(userId);
        comment.setCreated(System.currentTimeMillis());
        Integer count = commentApi.saveComment(comment);
        //4.拼接RedisKey 将用户的点赞状态存入Redis中
        String key = Constants.MOVEMENTS_INTERACT_KEY + monmentId;
        String hashKey = Constants.MOVEMENT_LOVE_HASHKEY + userId;
        //key:动态id 相当于一个命名空间 空间就是这条动态
        //Redis的hash 是一个string类型的key和value的映射表，这里的value是一系列的键值对，hash特别适合用于存储对象。
        //哈希类型的数据操作总的思想是通过key和field操作value，key是数据标识，field是域，value是我们感
        //兴趣的业务数据。
        redisTemplate.opsForHash().put(key, hashKey , "1 ");

        return count;
    }

    public Integer unlove(String monmentId) {
        mqMessageService.sendLogMessage(UserThreadLocalHolder.getId(), "0207", "movement", monmentId);
        Long userId = UserThreadLocalHolder.getId();
        //1.调用api查看是否已经点赞
        Boolean isLiked = commentApi.isLiked(monmentId, userId, CommentType.LOVE);
        //2.如果未点赞 抛出异常
        if (!isLiked){
            throw new BuinessException(ErrorResult.disloveError());
        }
        //3.如果点赞 调用api  删除数据 返回点赞数量
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(monmentId));
        comment.setCommentType(CommentType.LOVE.getType());
        comment.setUserId(userId);
        Integer count = commentApi.deleteComment(comment);
        //拼接redis 的key  删除点赞状态
        String key = Constants.MOVEMENTS_INTERACT_KEY + monmentId;
        String hashKey = Constants.MOVEMENT_LOVE_HASHKEY + userId;
        redisTemplate.opsForHash().delete(key, hashKey);

        return count;
    }

    //动态底下的评论的点赞
    public Integer commentLike(String commentId) {
        //根据commentId 调用api给comment的likeCount + 1;
        Integer likeCount = commentApi.commentLike(commentId);
        return likeCount;
    }

    //取消动态下评论的点赞
    public Integer commentDislike(String commentId) {
        Integer likeCount = commentApi.commentDislike(commentId);
        return likeCount;
    }

    //查看谁看了我 (首页显示
    public List<VisitorsVo> queryVisitorsList() {
        // 如果已经点进去查看了完整的访客列表 那么会在redis中存放有具体的查看事件
        //在这事件之前的访客就不用展示了
        Long userId = UserThreadLocalHolder.getId();

        //1.查询访问时间 redis
        String key = Constants.VISITORS_USER;
        String hashKey = String.valueOf(userId);
        String value = (String) redisTemplate.opsForHash().get(key, hashKey);
        Long date = null;
        if (value != null){
             date = Long.valueOf(value);
        }
        //2.根据访问时间 查询之后的访问者
        List<Visitors> visitorsList = vistorsApi.getVisitorsList(date ,userId);
        if (visitorsList == null || visitorsList.size() == 0){
            return new ArrayList<>();
        }
        //3.老规矩 提取出用户idList  根据用户idList 查询用户的信息
        List<Long> visitorUserIdList = CollUtil.getFieldValues(visitorsList, "visitorUserId", Long.class);
        Map<Long, UserInfo> userInfoMap = userInfoApi.getUserInfoMap(visitorUserIdList, null);
        //4.最后根据userinfo 和 Visitors 对象构建VO对象
        ArrayList<VisitorsVo> voList = new ArrayList<>();
        for (Visitors visitors : visitorsList){
            UserInfo userInfo = userInfoMap.get(visitors.getVisitorUserId());
            if (userInfo != null){
                VisitorsVo vo = VisitorsVo.init(userInfo, visitors);
                voList.add(vo);
            }
        }
        //5.返回
        return voList;
    }

    //小视频的点赞
}
