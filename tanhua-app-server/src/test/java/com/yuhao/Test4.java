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
}
