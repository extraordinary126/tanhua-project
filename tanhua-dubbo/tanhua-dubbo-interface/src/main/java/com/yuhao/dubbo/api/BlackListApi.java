package com.yuhao.dubbo.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yuhao.bean.UserInfo;

public interface BlackListApi {
    IPage<UserInfo> getBlacklistByUserId(Long userID, int page, int pageSize);

    void cancelBlackList(Long uid);
}
