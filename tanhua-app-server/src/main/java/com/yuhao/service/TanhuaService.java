package com.yuhao.service;

import com.yuhao.VO.TodayBest;
import com.yuhao.bean.Mongo.RecommendUser;
import com.yuhao.bean.UserInfo;
import com.yuhao.dubbo.api.RecommendUserApi;
import com.yuhao.dubbo.api.UserInfoApi;
import com.yuhao.interceptor.UserThreadLocalHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Service
public class TanhuaService {

    @DubboReference
    private RecommendUserApi recommendUserApi;

    @DubboReference
    private UserInfoApi userInfoApi;


    public TodayBest getTodayBest() {
        //1.获取当前userId
        Long userId = UserThreadLocalHolder.getId();
        //2.调用api查询
        RecommendUser recommendUser = recommendUserApi.queryWithMaxScore(userId);
        if (recommendUser == null){
            //如果没有推荐的用户 那么设置一个默认值
            recommendUser = new RecommendUser();
            recommendUser.setUserId(1L);
            recommendUser.setScore(0D);
        }
        //将RecommendUser对象转换为TodayBest  VO对象返回
        //VO对象中包含了UserInfo对象的信息
        // 所以通过RecommendUser对象中的推荐用户的userId查询该用户的UserInfo
        Long recommenderId = recommendUser.getUserId();
        UserInfo userInfo = userInfoApi.getUserInfo(recommenderId);
        //RecommendUser对象转换为TodayBest
        TodayBest vo = TodayBest.init(userInfo, recommendUser);
        return vo;
    }
}
