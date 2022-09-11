package com.yuhao.dubbo.api;

import com.yuhao.VO.PageResult;
import com.yuhao.bean.Mongo.RecommendUser;

import java.util.List;

public interface RecommendUserApi {

    RecommendUser queryWithMaxScore(Long toUserID);

    PageResult getRecommendUserList(Integer page, Integer pagesize, Long toUserId);

    RecommendUser getTwoPeopleScore(Long userId, Long toUserId);

    //查询推荐用户列表
    List<RecommendUser> queryCardsList(Long id, int i);
}
