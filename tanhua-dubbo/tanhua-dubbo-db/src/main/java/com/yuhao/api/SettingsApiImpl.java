package com.yuhao.api;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.yuhao.bean.Settings;
import com.yuhao.bean.User;
import com.yuhao.dubbo.api.SettingsApi;
import com.yuhao.mappers.SettingsMapper;
import com.yuhao.mappers.UserMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class SettingsApiImpl implements SettingsApi {

    @Autowired
    SettingsMapper settingsMapper;

    @Autowired
    UserMapper userMapper;

    //根据用户id 查询
     
    @Override
    public Settings getSettings(Long userId) {
        LambdaUpdateWrapper<Settings> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Settings::getUserId, userId);
        Settings settings = settingsMapper.selectOne(lambdaUpdateWrapper);
        //数据库中没有设置 就赋予默认设置
        if (settings == null){
            settings = new Settings();
            settings.setUserId(userId);
        }
        return settings;
    }

    @Override
    public void setNofication(Settings settings) {
        LambdaUpdateWrapper<Settings> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Settings::getUserId, settings.getUserId());
        int update = settingsMapper.update(settings, lambdaUpdateWrapper);
        if (update == 0){
            settingsMapper.insert(settings);
        }
    }

    @Override
    public void updatePhoneNumber(String oldPhoneNumber, String newPhoneNumber) {
        User user = new User();
        user.setMobile(newPhoneNumber);
        LambdaUpdateWrapper<User> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(User::getMobile, oldPhoneNumber);
        userMapper.update(user, lambdaUpdateWrapper);
    }


}
