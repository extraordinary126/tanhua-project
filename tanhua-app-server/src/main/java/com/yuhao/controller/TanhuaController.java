package com.yuhao.controller;

import com.yuhao.VO.TodayBest;
import com.yuhao.service.TanhuaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tanhua")
public class TanhuaController {

    @Autowired
    private TanhuaService tanhuaService;

    //今日佳人 返回得分最高的
    @GetMapping("/todayBest")
    public ResponseEntity getTodayBest(){

        TodayBest todayBestVO = tanhuaService.getTodayBest();
        return ResponseEntity.ok(todayBestVO);
    }
}
