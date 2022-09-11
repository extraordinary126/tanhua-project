package com.yuhao.controller;

import com.yuhao.VO.NearUserVo;
import com.yuhao.VO.PageResult;
import com.yuhao.VO.TodayBest;
import com.yuhao.dto.RecommendUserDto;
import com.yuhao.service.TanhuaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    /**
     * 探花-推荐用户列表
     */
    @GetMapping("/cards")
    public ResponseEntity queryCardsList() {
        List<TodayBest> list = tanhuaService.queryCardsList();
        return ResponseEntity.ok(list);
    }

    ///tanhua/:id/love
    //左滑喜欢
    @GetMapping("/{id}/love")
    public ResponseEntity rightLove(@PathVariable("id") Long id){
        tanhuaService.rightLove(id);
        return ResponseEntity.ok(null);
    }
    ///tanhua/:id/love
    //左滑喜欢
    @GetMapping("/{id}/unlove")
    public ResponseEntity leftUnlove(@PathVariable("id") Long id){
        tanhuaService.leftUnlove(id);
        return ResponseEntity.ok(null);
    }

    /**
     * 搜附近
     */
    @GetMapping("/search")
    public ResponseEntity<List<NearUserVo>> queryNearUser(String gender,
                                                          @RequestParam(defaultValue = "2000") String distance) {
        List<NearUserVo> list = this.tanhuaService.queryNearUser(gender, distance);
        return ResponseEntity.ok(list);
    }
}
