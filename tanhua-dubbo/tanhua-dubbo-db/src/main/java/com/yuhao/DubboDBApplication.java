package com.yuhao;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yuhao.mappers")        //扫描mapper文件
public class DubboDBApplication {

    public static void main(String[] args) {
        SpringApplication.run(DubboDBApplication.class, args);
    }
}
