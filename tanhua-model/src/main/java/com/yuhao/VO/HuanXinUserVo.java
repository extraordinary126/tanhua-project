package com.yuhao.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HuanXinUserVo implements Serializable {
    private String username;
    private String password;
}