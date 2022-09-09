package com.yuhao.controller;

import com.yuhao.VO.HuanXinUserVo;
import com.yuhao.service.HuanXinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/huanxin")
public class HuanXinController {

    @Autowired
    private HuanXinService huanXinService;

    //查询环信的账号密码
    @GetMapping("/user")
    public ResponseEntity user(){
        HuanXinUserVo huanXinUserVo = huanXinService.findHuanXinUser();
        return ResponseEntity.ok(huanXinUserVo);
    }
}
