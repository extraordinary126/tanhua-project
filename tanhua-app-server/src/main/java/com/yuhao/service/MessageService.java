package com.yuhao.service;

import com.yuhao.VO.*;
import com.yuhao.bean.Announcement;
import com.yuhao.bean.Mongo.Comment;
import com.yuhao.bean.Mongo.Friend;
import com.yuhao.bean.User;
import com.yuhao.bean.UserInfo;
import com.yuhao.common.utils.Constants;
import com.yuhao.dubbo.api.*;
import com.yuhao.enums.CommentType;
import com.yuhao.exception.BuinessException;
import com.yuhao.interceptor.UserThreadLocalHolder;
import com.yuhao.tanhua.autoconfig.template.HuanXinTemplate;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class MessageService {

    @DubboReference
    UserInfoApi userInfoApi;

    @DubboReference
    UserApi userApi;

    @Autowired
    HuanXinTemplate huanXinTemplate;

    @DubboReference
    FriendApi friendApi;

    @DubboReference
    CommentApi commentApi;

    @DubboReference
    AnnouncementApi announcementApi;


    //通过huanxinId获取userInfo
    public UserInfoVO getUserInfoByHuanXin(String huanxinId) {
        User user = userApi.getUserInfoByHuanXin(huanxinId);
        Long userId = user.getId();
        UserInfo userInfo = userInfoApi.getUserInfo(userId);
        UserInfoVO vo = new UserInfoVO();
        BeanUtils.copyProperties(userInfo, vo);
        if (userInfo.getAge() != null) {
            vo.setAge(String.valueOf(userInfo.getAge()));
        }
        return vo;
    }

    //添加好友  传过来要添加的id
    public void addFriend(Long userId) {
        //1.将好友信息注册到环信   传入当前用户和要添加的用户
        Boolean success = huanXinTemplate.addContact(Constants.HX_USER_PREFIX + userId,
                Constants.HX_USER_PREFIX + UserThreadLocalHolder.getId());
        //2.如果注册成功 记录到mongoDB;
        if (!success) {
            throw new BuinessException(ErrorResult.error());
        }
        friendApi.addFriend(userId, UserThreadLocalHolder.getId());

    }

    //查询联系人 根据keyword
    public PageResult queryFriends(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer pagesize, String keyword) {

        Long userId = UserThreadLocalHolder.getId();
        //查询出好友Friend的List 里面只有好友id
        List<Friend> friendsList = friendApi.queryFriends(userId, page, pagesize);

        //没有查到好友
        if (friendsList == null || friendsList.size() == 0) {
            return new PageResult();
        }

        List<Long> friendIdList = new ArrayList<>();
        //将friendList里的好友id存放到 好友IdList
        friendsList.stream().forEach(friend -> friendIdList.add(friend.getFriendId()));

        UserInfo queryInfo = new UserInfo();
        queryInfo.setNickname(keyword);
        Map<Long, UserInfo> userInfoMap = userInfoApi.getUserInfoMap(friendIdList, queryInfo);

        List<ContactVo> voList = new ArrayList<>();
        //从id集合中取出id 用来在用户信息Map中查出用户信息UserInfo  然后转换成vo对象添加到新的集合
        friendIdList.stream().forEach(id -> {
            UserInfo userInfo = userInfoMap.get(id);
            //根据昵称过滤已经写在userInfoApi的getUserInfoMap()方法里了
            //if (userInfo.getNickname().contains(keyword)){

            //如果该id对应的userinfo没有 在查询UserInfoMap时被过滤 那么就 转换成vo对象存入list
            if (userInfo != null) {
                ContactVo vo = ContactVo.init(userInfo);
                voList.add(vo);
            }
        });
        //根据vo集合构建PageResult对象
        PageResult pageResult = new PageResult(page, pagesize, 0, voList);
        return pageResult;
    }

    //看看谁点赞我了  查询出他的信息   所以Comment的publisheId 是我的UserId
    public PageResult getWhoCommentsMe(Integer page, Integer pagesize, CommentType commentType) {
        Long currentId = UserThreadLocalHolder.getId();
        List<Comment> commentList = commentApi.getCommentsList(currentId ,page, pagesize, commentType);

        Map<Long, Long> createdTime = new HashMap<>();
        //得到userId List
        List<Long> userIdList = new ArrayList<>();
        for (Comment comment : commentList){
            Long userId = comment.getUserId();

            //记录下点赞的时间
            createdTime.put(userId, comment.getCreated());
            userIdList.add(userId);
        }
        //根据userId List查询用户信息
        Map<Long, UserInfo> userInfoMap = userInfoApi.getUserInfoMap(userIdList, null);

        List<LikeListVo> voList = new ArrayList<>();
        for (Long userId : userIdList){
            UserInfo userInfo = userInfoMap.get(userId);
            LikeListVo likeListVo = new LikeListVo();
            likeListVo.setId(userInfo.getId().toString());
            likeListVo.setAvatar(userInfo.getAvatar());
            likeListVo.setNickname(userInfo.getNickname());
            //从map中取出点赞时间  转换成日期字符串
            String createdTimeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createdTime.get(userId));
            likeListVo.setCreateDate(createdTimeStr);
            voList.add(likeListVo);
        }

        return new PageResult(page, pagesize, 0, voList);
    }


    //获取公告getAnnouncements
    public PageResult getAnnouncements(Integer page, Integer pagesize) {
        List<Announcement> announcementList = announcementApi.getAnnouncements(page, pagesize);

        List<AnnouncementVo> voList = new ArrayList<>();
        for (Announcement announcement : announcementList){
            AnnouncementVo vo = new AnnouncementVo();
            BeanUtils.copyProperties(announcement, vo);
            Date created = announcement.getCreated();
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(created);
            vo.setCreateDate(date);
            voList.add(vo);
        }
        return new PageResult(page, pagesize, 0, voList);
    }

    public void deleteFriend(Long likeUserId) {
        Long currentUserId = UserThreadLocalHolder.getId();
        huanXinTemplate.deleteContact(Constants.HX_USER_PREFIX + likeUserId, Constants.HX_USER_PREFIX + likeUserId);
    }
}
