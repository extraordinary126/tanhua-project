package com.yuhao.api;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuhao.bean.BlackList;
import com.yuhao.bean.UserInfo;
import com.yuhao.dubbo.api.BlackListApi;
import com.yuhao.mappers.BlackListMapper;
import com.yuhao.mappers.UserInfoMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class BlackListApiImpl implements BlackListApi {

    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    BlackListMapper blackListMapper;
    
    //分页查询黑名单列表  构造一个UserInfo对象返回黑名单信息
    @Override
    public IPage<UserInfo> getBlacklistByUserId(Long userID, int page, int pageSize) {
        //1.构建分页参数对象Page
        Page pageInfo = new Page(page, pageSize);
        //2. 调用自定义方法分页 (分页对象Page, 常规的SQL条件参数)
        return userInfoMapper.getBlackListPage(pageInfo, userID);
    }

    @Override
    public void cancelBlackList(Long uid) {
        LambdaUpdateWrapper<BlackList> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(BlackList::getBlackUserId, uid);
        blackListMapper.delete(lambdaUpdateWrapper);
    }
}
