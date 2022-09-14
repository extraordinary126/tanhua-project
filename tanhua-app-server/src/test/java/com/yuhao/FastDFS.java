package com.yuhao;

import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class FastDFS {

   //测试FastDFS
    //文件上传

    //用于文件的上传或者下载
    @Autowired
    private FastFileStorageClient client;

    @Autowired
    private FdfsWebServer webServer;

    @Test
    public void test() throws FileNotFoundException {
        //1.指定文件
        File file = new File("/Users/yuhao/Downloads/ba68008f-62db-4424-8632-faba24c2e50c.jpeg");
        //2.文件上传
        StorePath path = client.uploadFile(new FileInputStream(file), file.length(), "rar", null);
        //3.拼接请求路径
        String fullPath = path.getFullPath();
        System.out.println(fullPath);

        //拼接http地址
        String url = webServer.getWebServerUrl() + fullPath;
        System.out.println(url);
    }
}