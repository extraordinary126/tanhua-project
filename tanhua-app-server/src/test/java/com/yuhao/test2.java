package com.yuhao;

import com.yuhao.bean.User;
import com.yuhao.dubbo.api.UserApi;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class test2 {

    @DubboReference
    private UserApi userApi;

    @Test
    public void test(){
        User user = userApi.findByMobile("13500000000");
        System.out.println(user);  
    }
}
