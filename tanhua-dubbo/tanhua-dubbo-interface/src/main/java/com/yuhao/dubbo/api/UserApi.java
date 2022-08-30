package com.yuhao.dubbo.api;

import com.yuhao.bean.User;

public interface UserApi {

    User findByMobile(String mobile);

    Long save(User user);
}
