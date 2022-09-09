package com.yuhao.controller;

import com.yuhao.VO.PageResult;
import com.yuhao.VO.TodayBest;
import com.yuhao.dto.RecommendUserDto;
import com.yuhao.service.TanhuaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    ///tanhua/recommendation  推荐朋友  get
    @GetMapping("/recommendation")
    public ResponseEntity recommendation(RecommendUserDto recommendUserDto){
        PageResult pageResult = tanhuaService.recommendation(recommendUserDto);
        return ResponseEntity.ok(pageResult);
    }

    //tanhua/:id/personalInfo
    //查看佳人信息
    @GetMapping("/{id}/personalInfo")
    public ResponseEntity personalInfo(@PathVariable Long id){
        TodayBest todayBest = tanhuaService.getPersonalInfo(id);
        return ResponseEntity.ok(todayBest);
    }

    ///tanhua/strangerQuestions
    @GetMapping("/strangerQuestions")
    public ResponseEntity getStrangerQuestions(Long userId){
        String question = tanhuaService.getStrangerQuestions(userId);
        return ResponseEntity.ok(question);
    }

    ///tanhua/strangerQuestions
    @PostMapping("/strangerQuestions")
    public ResponseEntity replyStrangerQuestions(@RequestBody Map map){
        String obj = map.get("userId").toString();   //感兴趣的用户id
        Long userId = Long.valueOf(obj);
        String reply = (String) map.get("reply");       //回复的内容
        tanhuaService.replyStrangerQuestions(userId, reply);
        return ResponseEntity.ok(null);
    }
}
