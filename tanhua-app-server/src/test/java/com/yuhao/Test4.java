package com.yuhao;

import com.yuhao.bean.Mongo.RecommendUser;
import com.yuhao.dubbo.api.RecommendUserApi;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class Test4 {

    @DubboReference
    RecommendUserApi recommendUserApi;

    @Test
    public void aipFace(){
        RecommendUser recommendUser = recommendUserApi.queryWithMaxScore(2L);
        System.out.println(recommendUser);
    }

    @Test
    public void test1(){
        String s1 = "MOVEMENTS_INTERACT_609cf6538743d448c02c61ed";
        String s2 = "MOVEMENTS_INTERACT_609cf6538743d448c02c61ed";
        System.out.println(s1.equals(s2));
        String s3 = "MOVEMENT_LIKE_1";
        String s4 = "MOVEMENT_LIKE_1";
        System.out.println(s3.equals(s4));

        Number number = -1;
        Number number1 = +1;
        System.out.println(number1);

        String s = "av.mp4";
        String fileEnd = s.substring(s.lastIndexOf(".") + 1);
        System.out.println(fileEnd);
    }

    @Test
    public void test1211(){
    }
}
