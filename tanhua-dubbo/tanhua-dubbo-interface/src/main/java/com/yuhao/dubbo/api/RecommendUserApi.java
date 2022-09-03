package com.yuhao.dubbo.api;

import com.yuhao.VO.PageResult;
import com.yuhao.bean.Mongo.RecommendUser;

public interface RecommendUserApi {

    RecommendUser queryWithMaxScore(Long toUserID);

    PageResult getRecommendUserList(Integer page, Integer pagesize, Long toUserId);
}
