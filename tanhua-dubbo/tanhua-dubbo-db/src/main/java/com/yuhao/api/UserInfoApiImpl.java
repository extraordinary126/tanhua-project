package com.yuhao.api;

import com.yuhao.bean.UserInfo;
import com.yuhao.dubbo.api.UserInfoApi;
import com.yuhao.mappers.UserInfoMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class UserInfoApiImpl implements UserInfoApi {

    @Autowired
    UserInfoMapper userInfoMapper;

    @Override
    public void save(UserInfo userInfo) {
        userInfoMapper.insert(userInfo);
    }

    @Override
    public void update(UserInfo userInfo) {
        userInfoMapper.updateById(userInfo);
    }

    @Override
    public UserInfo getUserInfo(Long id) {
        UserInfo userInfo = userInfoMapper.selectById(id);
//        UserInfoVO vo = new UserInfoVO();
//        //将userinfo 中的值 copy到 vo中  非同名同类型的不会copy
//        BeanUtils.copyProperties(userInfo, vo);
//        if (userInfo.getAge() != null){
//            vo.setAge(userInfo.getAge().toString());
//        }
        return userInfo;
    }


}