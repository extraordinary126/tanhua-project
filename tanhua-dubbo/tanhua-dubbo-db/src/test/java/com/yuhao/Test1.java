package com.yuhao;


import com.yuhao.mappers.UserInfoMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DubboDBApplication.class)
public class Test1 {

    @Resource
    UserInfoMapper userInfoMapper;

    @Test
    public void test1(){
        for (int i = 22; i < 91; i++) {
            userInfoMapper.updateNickname((long) i, "BOT_NO." + i);
        }

    }

}
