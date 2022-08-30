package com.yuhao.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor  //满参构造方法
@NoArgsConstructor   //无参构造方法
public class User extends BasePojo {     //dubbo传输需要实现序列化

    private Long id;
    private String mobile;
    private String password;

}
