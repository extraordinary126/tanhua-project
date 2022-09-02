package com.yuhao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

//启动类
@SpringBootApplication(exclude ={
        MongoAutoConfiguration.class,   //app-server不需要mongoDB  排除自动装配的配置
        MongoDataAutoConfiguration.class
}
)
public class AppServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppServerApplication.class,args);
    }
}
