package com.yuhao.service;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.PageUtil;
import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.yuhao.VO.CommentVo;
import com.yuhao.VO.ErrorResult;
import com.yuhao.VO.PageResult;
import com.yuhao.VO.VideoVo;
import com.yuhao.bean.Mongo.Comment;
import com.yuhao.bean.Mongo.FocusUser;
import com.yuhao.bean.Mongo.Video;
import com.yuhao.bean.UserInfo;
import com.yuhao.common.utils.Constants;
import com.yuhao.dubbo.api.CommentApi;
import com.yuhao.dubbo.api.UserInfoApi;
import com.yuhao.dubbo.api.VideoApi;
import com.yuhao.enums.CommentType;
import com.yuhao.exception.BuinessException;
import com.yuhao.interceptor.UserThreadLocalHolder;
import com.yuhao.tanhua.autoconfig.template.OssTemplate;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SmallVideosService {

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Autowired
    private FdfsWebServer fdfsWebServer;

    @DubboReference
    private VideoApi videoApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private CommentApi commentApi;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    //上传视频   封面图片   封面文件
    public void saveVideos(MultipartFile videoThumbnail, MultipartFile videoFile) throws IOException {

        if (videoFile == null && videoThumbnail == null) {
            //必须上传否则报错
            throw new BuinessException(ErrorResult.error());
        }
        //1.封面图片上传到阿里云OSS 获取地址
        String imageUrl = ossTemplate.upload(videoThumbnail.getOriginalFilename(), videoThumbnail.getInputStream());
        //2.视频上传到fdfs  并获取地址
        String fileName = videoFile.getOriginalFilename();  //bacd.mp4
        //以点来截取字符串 获取后缀名  不 + 1 就是 .mp4
        String end = fileName.substring(fileName.lastIndexOf(".") + 1);
        StorePath storePath = fastFileStorageClient.uploadFile(videoFile.getInputStream(),
                videoFile.getSize(), end, null);
        String videoUrl = fdfsWebServer.getWebServerUrl() + storePath.getFullPath();
        //3.获取video对象
        Video video = new Video();
        video.setUserId(UserThreadLocalHolder.getId());
        video.setVideoUrl(videoUrl);
        video.setPicUrl(imageUrl);
        video.setText("写下视频对应的文本内容吧");
        // 4.调用api完成保存
        String videoId = videoApi.save(video);
        if (StringUtils.isEmpty(videoId)) {
            throw new BuinessException(ErrorResult.error());
        }
    }


    @Cacheable(value = "videos", key = "T(com.yuhao.interceptor.UserThreadLocalHolder).getId()+'_'+#page+'_'+#pagesize")
    // userId_page_pagesize
    public PageResult queryVideoList(Integer page, Integer pagesize) {
        //1.查询redis数据  分页的数据
        String key = Constants.VIDEOS_RECOMMEND + UserThreadLocalHolder.getId();
        String value = redisTemplate.opsForValue().get(key);
        //2.判断redis数据是否有数据  是否满足本次分页的条数
        List<Video> list = new ArrayList<>();
        int redisPage = 0;
        if (!StringUtils.isEmpty(value)) {
            //3.如果redis数据存在 那么根据redis中的vid 查询视频
            String[] values = value.split(",");
            //判断当前页的起始条数是否小于数组总数
            if (values.length > (page - 1) * pagesize) {
                //使用stream流 来过滤数据
                List<Long> vids = Arrays.stream(values).skip((page - 1) * pagesize).limit(pagesize)
                        //将数据转换为long类型
                        .map(s -> Long.valueOf(s))
                        //收集数据为list
                        .collect(Collectors.toList());
                //5.调用api 通过视频vid 查询视频
                list = videoApi.findVideosByIds(vids);
            }
            //计算出redisPage 一共有几页
            redisPage = PageUtil.totalPage(value.length(), pagesize);
        }
        //4.如果redis数据不存在 分页查询数据
        if (list == null || list.size() == 0) {
            // page:  传入的页码数  -  redis中已经查询过的页数
            list = videoApi.findVideosList(page - redisPage, pagesize);
        }
        //5.提取视频列表中的用户id  查询用户信息  构造返回值
        List<Long> userIdList = CollUtil.getFieldValues(list, "userId", Long.class);
        Map<Long, UserInfo> userInfoMap = userInfoApi.getUserInfoMap(userIdList, null);

        List<VideoVo> voList = new ArrayList<>();
        for (Video video : list) {
            UserInfo userInfo = userInfoMap.get(video.getUserId());
            if (userInfo != null) {
                VideoVo vo = VideoVo.init(userInfo, video);

                //判断是否关注 从redis获取
                String focusKey = Constants.FOCUS_USER;
                String hashKey = UserThreadLocalHolder.getId().toString();
                String videoUserId = (String) redisTemplate.opsForHash().get(focusKey, hashKey);
                //如果redis中数据显示 关注列表id和当前取出的视频发布者userid一致
                if (!StringUtils.isEmpty(videoUserId) && videoUserId.equals(vo.getUserId().toString())) {
                    vo.setHasFocus(1);
                }
                //判断是否点赞 从redis获取
                String likeKey = Constants.VIDEO_LIKE_KEY;
                hashKey = vo.getId();
                videoUserId = (String) redisTemplate.opsForHash().get(likeKey, hashKey);
                if (!StringUtils.isEmpty(videoUserId) && (UserThreadLocalHolder.getId().toString()).equals(videoUserId)) {
                    //redis中存的key  和当前登录的用户id 相同 说明已经点过赞了
                    vo.setHasLiked(1);
                }
                //设置评论数量
                Object redisValue = redisTemplate.opsForHash().get(Constants.VIDEO_COMMENT_KEY, vo.getId());
                if (redisValue == null){
                    //没有评论 那就是0 不用设置直接存入
                    vo.setCommentCount(0);
                    voList.add(vo);
                    continue;
                }
                Integer count = Integer.valueOf(redisValue.toString());
                vo.setCommentCount(count);
                voList.add(vo);
            }
        }
        return new PageResult(page, pagesize, 0, voList);
    }

    //关注视频作者
    public void userFocus(Long videoUserId) {
        Long currentUserId = UserThreadLocalHolder.getId();
        FocusUser focusUser = new FocusUser(null, currentUserId, videoUserId, null);
        Boolean flag = videoApi.userFocus(focusUser);
        //存入redis 已经关注 Hash
        if (flag) {
            String key = Constants.FOCUS_USER;
            String hashKey = currentUserId.toString();
            redisTemplate.opsForHash().put(key, hashKey, videoUserId.toString());
        }
    }

    public void userUnFocus(Long videoUserId) {
        Long currentUserId = UserThreadLocalHolder.getId();
        Boolean flag = videoApi.userUnFocus(currentUserId, videoUserId);
        //如果成功 删除redis key
        if (flag) {
            String key = Constants.FOCUS_USER;
            String hashKey = currentUserId.toString();
            redisTemplate.opsForHash().delete(key, hashKey);
        }
    }

    //视频点赞
    public String videoLike(String id) {
        videoApi.videoLike(id);
        String key = Constants.VIDEO_LIKE_KEY;
        String value = UserThreadLocalHolder.getId().toString();
        redisTemplate.opsForHash().put(key, id, value);
        return UserThreadLocalHolder.getId().toString();
    }

    //取消点赞
    public void videoDislike(String id) {
        videoApi.videoDisLike(id);
        redisTemplate.opsForHash().delete(Constants.VIDEO_LIKE_KEY, id);
    }

    //查询视频的评论列表
    public PageResult commentsList(Integer page, Integer pagesize, String id) {
        List<Comment> comments = commentApi.getAllCommentsById(id, CommentType.COMMENT, page, pagesize);
        List<Long> userIdList = CollUtil.getFieldValues(comments, "userId", Long.class);
        if (userIdList == null || userIdList.size() ==0){
            return new PageResult();
        }
        Map<Long, UserInfo> userInfoMap = userInfoApi.getUserInfoMap(userIdList, null);
        List<CommentVo> voList = new ArrayList<>();
        for (Comment comment : comments){
            UserInfo userInfo = userInfoMap.get(comment.getUserId());
            if (userInfo != null){
                CommentVo vo = CommentVo.init(userInfo, comment);
                String key = Constants.VIDEO_COMMENT_LIKE_KEY + id;
                String hashkey = UserThreadLocalHolder.getId().toString();
                if (redisTemplate.opsForHash().hasKey(key, hashkey)){
                   vo.setHasLiked(1);
                }
                voList.add(vo);
            }
        }
        return new PageResult(page, pagesize, 0, voList);
    }

    //发布评论
    public Integer publishComment(String id, String content) {
       Long publisher =  videoApi.getVideoPublisher(id);

        Comment comment1 = new Comment(null, new ObjectId(id),CommentType.COMMENT.getType(),
               content , UserThreadLocalHolder.getId(), publisher, null,0);
        commentApi.publishVideoComment(comment1);
        Integer commentCount = videoApi.commentCountPlusOne(id) + 1;
        redisTemplate.opsForHash().put(Constants.VIDEO_COMMENT_KEY, id, commentCount.toString());
        return commentCount;
    }

    public Integer videoCommentLike(String id) {
        Long userId = UserThreadLocalHolder.getId();
        //1.查询用户是否已经点赞
        //2.如果已经点赞 抛出异常
        //3.如果未点赞 调用API将数据保存到MongoDB
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(id));
        comment.setCommentType(CommentType.LIKE.getType());
        comment.setUserId(userId);
        comment.setCreated(System.currentTimeMillis());
        Integer count = commentApi.saveVideoComment(comment);
        //4.拼接RedisKey 将用户的点赞状态存入Redis中
        String key = Constants.VIDEO_COMMENT_LIKE_KEY + id;
        String hashKey = userId.toString();
        //key:动态id 相当于一个命名空间 空间就是这条动态
        //Redis的hash 是一个string类型的key和value的映射表，这里的value是一系列的键值对，hash特别适合用于存储对象。
        //哈希类型的数据操作总的思想是通过key和field操作value，key是数据标识，field是域，value是我们感
        //兴趣的业务数据。
        redisTemplate.opsForHash().put(key, hashKey , "1");

        return count;
    }

    public Integer videoCommentDisLike(String id) {
        Long userId = UserThreadLocalHolder.getId();
        //3.如果点赞 调用api  删除数据 返回点赞数量
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(id));
        comment.setCommentType(CommentType.LIKE.getType());
        comment.setUserId(userId);
        Integer count = commentApi.deleteVideoCommentLike(comment);
        //拼接redis 的key  删除点赞状态
        String key = Constants.VIDEO_COMMENT_LIKE_KEY + id;
        String hashKey = userId.toString();
        redisTemplate.opsForHash().delete(key, hashKey);

        return count;
    }
}
