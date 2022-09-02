package com.yuhao.dubbo.api;

import com.yuhao.bean.UserInfo;

public interface UserInfoApi {

    void save(UserInfo userInfo);

    void update(UserInfo userInfo);

    UserInfo getUserInfo(Long id);

}
