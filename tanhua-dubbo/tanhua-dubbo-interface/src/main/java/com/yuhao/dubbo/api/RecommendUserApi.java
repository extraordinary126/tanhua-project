package com.yuhao.dubbo.api;

import com.yuhao.bean.Mongo.RecommendUser;

public interface RecommendUserApi {

    RecommendUser queryWithMaxScore(Long toUserID);
}
