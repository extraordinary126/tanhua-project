package com.yuhao;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.yuhao.tanhua.autoconfig.template.OssTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class OSSTest {

       @Autowired
       private OssTemplate ossTemplate;
        /** 
         * 案例：
         * 将资料中的1.jpg上传到阿里云OSS
         * 存放的位置   /yyyy/MM/dd/xxxx.jpg
         */
        @Test
        public void testOss() throws FileNotFoundException {

            //1、配置图片路径
            String path = "/Users/yuhao/Downloads/探花交友/探花交友/探花交友讲义资料/MD笔记/day02/assets/image-20210118110715714.png";
            //2、构造FileInputStream
            FileInputStream inputStream = new FileInputStream(new File(path));
            //3、拼写图片路径
            String filename = new SimpleDateFormat("yyyy/MM/dd").format(new Date())
                    + "/" + UUID.randomUUID().toString() + path.substring(path.lastIndexOf("."));

            // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
            String endpoint = "oss-cn-hangzhou.aliyuncs.com";
            String accessKeyId = "LTAI5tQgnbNQvJ1mGv34qGNG";
            String accessKeySecret = "LeMKlCtHzjEKqTJnBTqlDjj6lIhGxN";

            // 创建OSSClient实例。
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            // 填写Byte数组。
            // 填写Bucket名称和Object完整路径。Object完整路径中不能包含Bucket名称。
            ossClient.putObject("tanhuatanhua111", filename, inputStream);

            // 关闭OSSClient。
            ossClient.shutdown();

            String url = "https://tanhua001.oss-cn-beijing.aliyuncs.com/" + filename;
            System.out.println(url);
        }

        @Test
        public void test2() throws FileNotFoundException {
            String path = "/Users/yuhao/Desktop/IMG_5333.JPG";
            ossTemplate.upload(path,new FileInputStream(new File(path)));
        }

    }

