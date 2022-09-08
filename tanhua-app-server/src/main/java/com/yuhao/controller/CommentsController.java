package com.yuhao.controller;

import com.yuhao.VO.PageResult;
import com.yuhao.service.CommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/comments")
public class CommentsController {

    @Autowired
    CommentsService commentsService;

    //      /comments POST 提交评论
    @PostMapping
    public ResponseEntity comments(@RequestBody Map map){
        String movementId = (String) map.get("movementId"); //动态编号
        String comment = (String) map.get("comment");       //评论内容
        commentsService.comments(movementId, comment);
        return ResponseEntity.ok(null);
    }

    @GetMapping
    public ResponseEntity showComments(String movementId, Integer page, Integer pagesize){
        PageResult pageResult = commentsService.showComments(movementId, page, pagesize);
        return ResponseEntity.ok(pageResult);
    }

    ///comments/:id/like
    //动态底下的评论 点赞功能
    @GetMapping("/{id}/like")
    public ResponseEntity commentLike(@PathVariable("id") String commentId){
        Integer likeCount = commentsService.commentLike(commentId);
        return ResponseEntity.ok(likeCount);
    }

    //comments/:id/dislike
    //动态下的评论取消点赞
    @GetMapping("/{id}/dislike")
    public ResponseEntity commentDislike(@PathVariable("id") String commentId){
        Integer likeCount = commentsService.commentDislike(commentId);
        return ResponseEntity.ok(likeCount);
    }
}
