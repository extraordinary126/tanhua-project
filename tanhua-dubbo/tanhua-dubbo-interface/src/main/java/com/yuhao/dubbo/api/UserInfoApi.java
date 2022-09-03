package com.yuhao.dubbo.api;

import com.yuhao.bean.UserInfo;

import java.util.List;
import java.util.Map;

public interface UserInfoApi {

    void save(UserInfo userInfo);

    void update(UserInfo userInfo);

    UserInfo getUserInfo(Long id);

    //批量查询用户详情
    // 根据id查询与他关联的userinfo
    //返回值  Map<id, UserInfo>
    Map<Long, UserInfo> getUserInfoMap(List<Long> userIds, UserInfo userinfo);
}
