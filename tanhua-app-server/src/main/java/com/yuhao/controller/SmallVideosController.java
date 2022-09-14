package com.yuhao.controller;

import com.yuhao.VO.PageResult;
import com.yuhao.dto.CommentDTO;
import com.yuhao.service.SmallVideosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/smallVideos")
public class SmallVideosController {

    @Autowired
    private SmallVideosService videosService;

    /**
     * 发布视频
     * 接口路径：POST
     * 请求参数：
     * videoThumbnail：封面图
     * videoFile：视频文件
     */
    @PostMapping
    public ResponseEntity saveVideos(MultipartFile videoThumbnail, MultipartFile videoFile) throws IOException {
        videosService.saveVideos(videoThumbnail, videoFile);
        return ResponseEntity.ok(null);
    }


    /**
     * 视频列表
     */
    @GetMapping
    public ResponseEntity queryVideoList(@RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult result = videosService.queryVideoList(page, pagesize);
        return ResponseEntity.ok(result);
    }

    //关注视频作者
    //smallVideos/:uid/userFocus
    @PostMapping("/{uid}/userFocus")
    public ResponseEntity userFocus(@PathVariable("uid") Long videoUserId) {
        videosService.userFocus(videoUserId);
        return ResponseEntity.ok(null);
    }

    //取关视频作者 ///smallVideos/:uid/userUnFocus
    @PostMapping("/{uid}/userUnFocus")
    public ResponseEntity userUnFocus(@PathVariable("uid") Long videoUserId) {
        videosService.userUnFocus(videoUserId);
        return ResponseEntity.ok(null);
    }

    //视频点赞
    ///smallVideos/:id/like
    @PostMapping("{id}/like")
    public ResponseEntity videoLike(@PathVariable("id") String id) {
        videosService.videoLike(id);
        return ResponseEntity.ok(null);
    }

    //视频取消点赞
    ///smallVideos/:id/dislike
    @PostMapping("{id}/dislike")
    public ResponseEntity videoDisLike(@PathVariable("id") String id) {
        videosService.videoDislike(id);
        return ResponseEntity.ok(null);
    }

    ///smallVideos/:id/comments
    //评论列表
    @GetMapping("/{id}/comments")
    public ResponseEntity commentsList(@PathVariable String id,
                                       @RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer pagesize){
        PageResult result = videosService.commentsList(page, pagesize, id);
        return ResponseEntity.ok(result);
    }

    ///smallVideos/:id/comments
    //发布评论
    @PostMapping("/{id}/comments")
    public ResponseEntity publishComment(@PathVariable String id,
                                         @RequestBody CommentDTO comment){
        Integer count = videosService.publishComment(id, comment.getComment());
        return ResponseEntity.ok(count);
    }

    ///smallVideos/comments/:id/like
    //视频评论点赞
    @PostMapping("/comments/{id}/like")
    public ResponseEntity videoCommentLike(@PathVariable String id){
        Integer integer = videosService.videoCommentLike(id);
        return ResponseEntity.ok(integer);
    }

    ///smallVideos/comments/:id/dislike
    //视频中评论取消点赞
    @PostMapping("/comments/{id}/dislike")
    public ResponseEntity videoCommentDisLike(@PathVariable String id){
        Integer integer = videosService.videoCommentDisLike(id);
        return ResponseEntity.ok(integer);
    }
}