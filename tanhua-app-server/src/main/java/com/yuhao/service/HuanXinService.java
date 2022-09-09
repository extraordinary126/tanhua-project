package com.yuhao.service;

import com.yuhao.VO.HuanXinUserVo;
import com.yuhao.bean.User;
import com.yuhao.dubbo.api.UserApi;
import com.yuhao.interceptor.UserThreadLocalHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Service
public class HuanXinService {

    @DubboReference
    private UserApi userApi;

    //查询当前用户的环信账号
    //1. 获取当前用户id  根据规则进行拼接
    //2.或者  获取当前用户id 查询用户
    public HuanXinUserVo findHuanXinUser() {
        Long userId = UserThreadLocalHolder.getId();
        User user = userApi.getUserById(userId);
        if (user == null){
            return null;
        }
        HuanXinUserVo vo = new HuanXinUserVo();
        vo.setUsername(user.getHxUser());
        vo.setPassword(user.getHxPassword());
        return vo;
    }
}
