package com.yuhao.controller;

import com.yuhao.VO.PageResult;
import com.yuhao.VO.UserInfoVO;
import com.yuhao.enums.CommentType;
import com.yuhao.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    MessageService messageService;

    //根据环信用户id  查询用户详情
    @GetMapping("/userinfo")
    public ResponseEntity getUserInfo(String huanxinId){
        UserInfoVO vo = messageService.getUserInfoByHuanXin(huanxinId);
        return ResponseEntity.ok(vo);
    }

    ///messages/contacts
    //添加好友
    @PostMapping("/contacts")
    public ResponseEntity addFriend(@RequestBody Map map){
        String userIdObj = map.get("userId").toString();   //要添加的userId
        Long userId = Long.valueOf(userIdObj);
        messageService.addFriend(userId);
        return ResponseEntity.ok(null);
    }

    ///messages/contacts  查看联系人列表
    @GetMapping("/contacts")
    public ResponseEntity queryFriends(@RequestParam(defaultValue = "1")Integer page,
                                       @RequestParam(defaultValue = "1")Integer pagesize,
                                       String keyword){

        PageResult pageResult = messageService.queryFriends(page, pagesize, keyword);
        return ResponseEntity.ok(pageResult);
    }

    ///messages/likes
    //获取点赞列表 谁给我点赞了 评论点赞喜欢用同一个service方法 只要Controller中传入不同的CommentType即可
    @GetMapping("/likes")
    public ResponseEntity getWhoLikesMe(@RequestParam(defaultValue = "1")Integer page,
                                        @RequestParam(defaultValue = "10")Integer pagesize){
        PageResult pageResult = messageService.getWhoCommentsMe(page, pagesize, CommentType.LIKE);
        return ResponseEntity.ok(pageResult);
    }

    ///messages/comments
    //获取评论列表 谁给我评论了  评论点赞喜欢用同一个service方法 只要Controller中传入不同的CommentType即可
    @GetMapping("/comments")
    public ResponseEntity getWhoCommentsMe(@RequestParam(defaultValue = "1")Integer page,
                                        @RequestParam(defaultValue = "10")Integer pagesize){
        PageResult pageResult = messageService.getWhoCommentsMe(page, pagesize, CommentType.COMMENT);
        return ResponseEntity.ok(pageResult);
    }

    ///messages/loves
    //获取喜欢列表 谁喜欢了我 评论点赞喜欢用同一个service方法 只要Controller中传入不同的CommentType即可
    @GetMapping("/loves")
    public ResponseEntity getWhoLovesMe(@RequestParam(defaultValue = "1")Integer page,
                                           @RequestParam(defaultValue = "10")Integer pagesize){
        PageResult pageResult = messageService.getWhoCommentsMe(page, pagesize, CommentType.LOVE);
        return ResponseEntity.ok(pageResult);
    }

    ///messages/announcements
    //获取公告列表   --> mysql的 tb_announcement 表
    @GetMapping("/announcements")
    public ResponseEntity getAnnouncements(@RequestParam(defaultValue = "1")Integer page,
                                        @RequestParam(defaultValue = "10")Integer pagesize){
        PageResult pageResult = messageService.getAnnouncements(page, pagesize);
        return ResponseEntity.ok(pageResult);
    }
}
