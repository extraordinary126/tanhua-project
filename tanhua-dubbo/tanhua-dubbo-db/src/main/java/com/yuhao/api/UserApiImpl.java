package com.yuhao.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yuhao.bean.User;
import com.yuhao.dubbo.api.UserApi;
import com.yuhao.mappers.UserMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService   //dubbo提供者 暴露接口
public class UserApiImpl implements UserApi {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User findByMobile(String mobile) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mobile",mobile);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public Long save(User user) {
        userMapper.insert(user);
        return user.getId();
    }
}
