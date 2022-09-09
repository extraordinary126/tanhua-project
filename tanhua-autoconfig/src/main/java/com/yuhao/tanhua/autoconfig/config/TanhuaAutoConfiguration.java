package com.yuhao.tanhua.autoconfig.config;

import com.yuhao.tanhua.autoconfig.properties.AipFaceProperties;
import com.yuhao.tanhua.autoconfig.properties.HuanXinProperties;
import com.yuhao.tanhua.autoconfig.properties.OssProperties;
import com.yuhao.tanhua.autoconfig.template.AipFaceTemplate;
import com.yuhao.tanhua.autoconfig.template.HuanXinTemplate;
import com.yuhao.tanhua.autoconfig.template.OssTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

//该注解的作用是使 OssProperties 这个类上标注的 @ConfigurationProperties 注解生效,并且会自动将这个类注入到 IOC 容器中
@EnableConfigurationProperties({
        OssProperties.class,
        AipFaceProperties.class,
        HuanXinProperties.class
})
public class TanhuaAutoConfiguration {

    @Bean 
    public OssTemplate ossTemplate(OssProperties properties){
        return new OssTemplate(properties);
    }

    @Bean
    public AipFaceTemplate aipFaceTemplate() {
        return new AipFaceTemplate();
    }

    @Bean
    public HuanXinTemplate huanXinTemplate(HuanXinProperties huanXinProperties){
        return new HuanXinTemplate(huanXinProperties);
    }
}
