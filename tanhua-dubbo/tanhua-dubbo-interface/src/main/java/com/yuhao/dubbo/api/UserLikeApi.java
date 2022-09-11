package com.yuhao.dubbo.api;

import java.util.Map;

public interface UserLikeApi {


    Boolean saveOrUpdateLike(Long currentUserId, Long likeUserId, Boolean isLike);

    //获取 喜欢数 被喜欢数 互相喜欢数
    Map<String, Integer> getLikeCount(Long currentUserId);
}
