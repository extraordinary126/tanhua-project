package com.yuhao.dubbo.api;

import com.yuhao.VO.PageResult;
import com.yuhao.bean.Mongo.FocusUser;
import com.yuhao.bean.Mongo.Video;

import java.util.List;

public interface VideoApi {

    //返回VideoId
    String save(Video video);

    //根据vids 查询视频
    List<Video> findVideosByIds(List<Long> vids);

    //分页查询
    List<Video> findVideosList(int page, Integer pagesize);

    //关注视频作者
    Boolean userFocus(FocusUser focusUser);

    //取关视频作者
    Boolean userUnFocus(Long currentUserId, Long videoUserId);

    Boolean videoLike(String vid);

    Boolean videoDisLike(String id);

    //根据视频id查询视频发布者
    Long getVideoPublisher(String id);

    //视频评论数量+1
    Integer commentCountPlusOne(String id);

    //根据id查询视频列表
    PageResult getVideoListById(Integer page, Integer pagesize, Long userId);
}
