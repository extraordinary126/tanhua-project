package com.yuhao.api;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuhao.bean.UserInfo;
import com.yuhao.dubbo.api.UserInfoApi;
import com.yuhao.mappers.UserInfoMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

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

    //批量查询userinfo  查出所有相关联的符合条件的 用户信息 存入map
    @Override                   //要查询的id放入List          查询条件封装到UserInfo对象中
    public Map<Long, UserInfo> getUserInfoMap(List<Long> userIds, UserInfo userinfo) {
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserInfo::getId, userIds);
        //对条件进行过滤  如果传入了条件 那么根据条件构造QueryWrapper
        if (userinfo != null){
            if (userinfo.getAge() != null){
                queryWrapper.lt(UserInfo::getAge, userinfo.getAge());
            }
            if (!StringUtils.isEmpty(userinfo.getGender())){
                queryWrapper.eq(UserInfo::getGender, userinfo.getGender());
            }
        }
        List<UserInfo> userInfos = userInfoMapper.selectList(queryWrapper);
        //转换为map集合 将id作为key
        Map<Long, UserInfo> map = CollUtil.fieldValueMap(userInfos, "id");
        return map;
    }


}