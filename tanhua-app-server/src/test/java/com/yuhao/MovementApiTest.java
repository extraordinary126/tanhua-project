package com.yuhao;

import com.yuhao.bean.Mongo.Movement;
import com.yuhao.dubbo.api.MomentApi;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class MovementApiTest {

    @DubboReference
    private MomentApi movementApi;

    @Test
    public void testPublish() {
        Movement movement = new Movement();
        movement.setUserId(126L);
        movement.setTextContent("你的酒窝没有酒，我却醉的像条狗");
        List<String> list = new ArrayList<>();
        list.add("https://tanhuatanhua111.oss-cn-hangzhou.aliyuncs.com/2022/08/31/8fdfd24a-a43e-434c-92bc-ab5b7f153c66.jpg");
        list.add("https://tanhuatanhua111.oss-cn-hangzhou.aliyuncs.com/2022/08/31/8fdfd24a-a43e-434c-92bc-ab5b7f153c66.jpg");
        movement.setMedias(list);
        movement.setLatitude("40.066355");
        movement.setLongitude("116.350426");
        movement.setLocationName("中国北京市昌平区建材城西路16号");
        movementApi.sendMomoent(movement);
    }
}