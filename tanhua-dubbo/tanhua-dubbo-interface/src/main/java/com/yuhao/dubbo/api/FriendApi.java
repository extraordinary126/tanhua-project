package com.yuhao.dubbo.api;

import com.yuhao.bean.Mongo.Friend;

import java.util.List;

public interface FriendApi {

    //添加好友
    void addFriend(Long friendId, Long userId);

    //查询好友 根据keyword
    List<Friend> queryFriends(Long userId, Integer page, Integer pagesize);
}
