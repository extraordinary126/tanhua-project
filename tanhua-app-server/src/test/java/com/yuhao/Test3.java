package com.yuhao;

import com.yuhao.tanhua.autoconfig.template.AipFaceTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class Test3 {

    @Autowired
    AipFaceTemplate aipFaceTemplate;

    @Test
    public void aipFace(){
        String url = "https://tanhua001.oss-cn-beijing.aliyuncs.com/2022/08/30/fe28f4a2-b266-44c1-ab36-d88db6cfcaf5.jpg";
        boolean detect = aipFaceTemplate.detect(url);
        System.out.println(detect ? "里面有人" : "里面没人");
    }
}
