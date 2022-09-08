package com.yuhao.controller;

import com.yuhao.VO.MovementsVo;
import com.yuhao.VO.PageResult;
import com.yuhao.bean.Mongo.Movement;
import com.yuhao.service.CommentsService;
import com.yuhao.service.MomentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/movements")
public class MomentController {

    @Autowired
    private MomentService momentService;

    @Autowired
    private CommentsService commentsService;

    //发布动态方法
    @PostMapping
    public ResponseEntity sendMoment(Movement movement, MultipartFile[] imageContent) throws IOException {
        momentService.sendMoment(movement, imageContent);
        return ResponseEntity.ok(null);
    }

    //movements/all get
    //查看我的动态方法
    @GetMapping("/all")
    public ResponseEntity getMoment(Long userId,
                                    @RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10 ")Integer pagesize){

        PageResult pageResult = momentService.getMoment(userId, page, pagesize);
        return ResponseEntity.ok(pageResult);
    }

    //movements GET
    //查询好友动态
    @GetMapping
    public ResponseEntity getFriendMoment(@RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer pagesize){

        PageResult pageResult = momentService.getFriendMoment(page, pagesize);
        return ResponseEntity.ok(pageResult);
    }
    //查询推荐的动态
    ///movements/recommend
    @GetMapping("/recommend")
    public ResponseEntity getRecommendMonments(@RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer pagesize){

        PageResult pageResult = momentService.getRecommendMonments(page, pagesize);
        return ResponseEntity.ok(pageResult);
    }

    //查询单条动态
    ///movements/:id    id是动态的pid
    @GetMapping("/{id}")
    public ResponseEntity getSingleMoment(@PathVariable("id") String movementId){
        MovementsVo movementsVo = momentService.getSingleMoment(movementId);
        return ResponseEntity.ok(movementsVo);
    }

    ///movements/:id/like
    //点赞  GET  id是动态的id
    @GetMapping("/{id}/like")
    public ResponseEntity like(@PathVariable("id") String movementId){
        Integer likeCount = commentsService.like(movementId);
        return ResponseEntity.ok(likeCount);
    }

    ///movements/:id/dislike
    //取消点赞  GET  id是动态的id
    @GetMapping("/{id}/dislike")
    public ResponseEntity dislike(@PathVariable("id") String movementId){
        Integer likeCount = commentsService.dislike(movementId);
        return ResponseEntity.ok(likeCount);
    }

    ///movements/:id/love
    @GetMapping("/{id}/love")
    public ResponseEntity love(@PathVariable("id") String movementId){
        Integer loveCount = commentsService.love(movementId);
        return ResponseEntity.ok(loveCount);
    }

    @GetMapping("/{id}/unlove")
    public ResponseEntity unlove(@PathVariable("id") String movementId){
        Integer loveCount = commentsService.unlove(movementId);
        return ResponseEntity.ok(loveCount);
    }
}
