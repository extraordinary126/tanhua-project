package com.yuhao;

import com.yuhao.bean.User;
import com.yuhao.common.utils.Constants;
import com.yuhao.dubbo.api.UserApi;
import com.yuhao.tanhua.autoconfig.template.HuanXinTemplate;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class IMTest {

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    @DubboReference
    UserApi userApi;

    @Test
    public void emtest(){
        huanXinTemplate.createUser("yuhao","123456");
    }

    //批量注册
    @Test
    public void register() {
        for (int i = 1; i < 137; i++) {
            User user = userApi.getUserById(Long.valueOf(i));
            if(user != null) {
                Boolean create = huanXinTemplate.createUser("hx" + user.getId(), Constants.INIT_PASSWORD);
                if (create){
                    user.setHxUser("hx" + user.getId());
                    user.setHxPassword(Constants.INIT_PASSWORD);
                    userApi.updateUser(user);
                }
            }
        }
    }
}
