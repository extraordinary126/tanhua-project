package com.yuhao.service;

import com.yuhao.VO.ErrorResult;
import com.yuhao.dubbo.api.UserLocationApi;
import com.yuhao.exception.BuinessException;
import com.yuhao.interceptor.UserThreadLocalHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Service
public class BaiduService {

    @DubboReference
    UserLocationApi userLocationApi;


    public void updateLocation(Double longitude, Double latitude, String address) {
        Boolean flag = userLocationApi.updateLocation(UserThreadLocalHolder.getId(), longitude, latitude, address);
        if (!flag){
            throw new BuinessException(ErrorResult.error());
         }
    }
}
