package com.tanhua.admin;

import com.yuhao.tanhua.autoconfig.template.AliyunGreenTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GreenTemplateTest {

    @Autowired
    private AliyunGreenTemplate greenTemplate;

    @Test
    public void test1() throws Exception {
//        Map<String, String> map = greenTemplate.greenTextScan("看骚逼+v");
//        map.forEach((k,v) -> System.out.println(k + "--" + v));

        List<String> list = new ArrayList<>();
        list.add("http://images.china.cn/site1000/2018-03/17/dfd4002e-f965-4e7c-9e04-6b72c601d952.jpg");
        Map<String, String> map = greenTemplate.imageScan(list);
        System.out.println("------------");
        map.forEach((k,v)-> System.out.println(k +"--" + v));
    }
}
