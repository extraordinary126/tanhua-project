package com.yuhao;

import com.yuhao.bean.UserInfo;
import com.yuhao.dubbo.api.UserInfoApi;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class UserInfoMapTest {

    @DubboReference
    UserInfoApi userInfoApi;

    @Test
    public void test(){
        List ids = new ArrayList<>();
        ids.add(1l);
        ids.add(3l);
        ids.add(126l);
        ids.add(125l);
        ids.add(2l);
        UserInfo userInfo = new UserInfo();
        userInfo.setAge(50);
        Map<Long, UserInfo> userInfoMap = userInfoApi.getUserInfoMap(ids, userInfo);
        System.out.println(userInfoMap);
    }
}
