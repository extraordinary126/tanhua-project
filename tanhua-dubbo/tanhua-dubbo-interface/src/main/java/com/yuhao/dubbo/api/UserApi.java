package com.yuhao.dubbo.api;

import com.yuhao.bean.User;

public interface UserApi {
    //根据手机号查询用户
    User findByMobile(String mobile);
    //保存用户
    Long save(User user);
    //根据id查询用户
    User getUserById(Long userId);

    //更新用户
    Integer updateUser(User user);

    //根据环信id查询用户
    User getUserInfoByHuanXin(String huanxinId);
}
