package com.yuhao.controller;

import com.yuhao.VO.UserInfoVO;
import com.yuhao.bean.UserInfo;
import com.yuhao.interceptor.UserThreadLocalHolder;
import com.yuhao.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UsersController {


    @Autowired
    UserInfoService userInfoService;
    /**
     * http://192.168.10.125:3000/project/19/interface/api/265
     * Headers：
     * 参数名称	    参数值	            是否必须	    示例	备注
     * Content-Type	application/json	    是
     * Authorization	    token	        是               令牌
     *
     * 参数名称	是否必须	示例	备注
     * userID	否       100     用户id，当不传递时，查询当前用户的资料信息
     *
     * 返回值
     */


    @GetMapping
    public ResponseEntity getUserInfo( Long userID,    // long?
                                      @RequestHeader("Authorization") String token){

        /*boolean verifyToken = JwtUtils.verifyToken(token);
        if (!verifyToken) {
            //不合法 重新登录
            return ResponseEntity.status(401).body(null);
        }*//*
        Claims claims = JwtUtils.getClaims(token);
        Integer id = (Integer) claims.get("id");*/
        Long id = UserThreadLocalHolder.getId();
        //如果没有传过来id 那么就查询当前token的id
        if (userID == null){
            userID = id;
        }
        UserInfoVO userInfoVO = userInfoService.getUserInfo(userID);
        return ResponseEntity.ok(userInfoVO);
    }

    //修改用户信息
    @PutMapping
    public ResponseEntity update(@RequestBody UserInfo userInfo,
                                 @RequestHeader("Authorization") String token){
        //1.判断token是否合法
        //拦截器已经判断了
        //2.保存信息页 userinfo 和 user不是一张表 用户没有ID 从token里拿
        /* Claims claims = JwtUtils.getClaims(token);
        Integer id = (Integer) claims.get("id");*/
        Long id = UserThreadLocalHolder.getId();
        userInfo.setId(id);
        userInfoService.update(userInfo);
        return ResponseEntity.ok(null);
    }

    ///users/header  Post  更新用户头像
    @PostMapping("/header")
    public ResponseEntity updateHeader(@RequestBody MultipartFile headPhoto,
                                        @RequestHeader("Authorization") String token        //拦截器会处理token并往ThreadLocal存入数据的
                                       ){
        Long id = UserThreadLocalHolder.getId();
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfoService.updateHead(headPhoto, id);
        return ResponseEntity.ok(null);
    }

    // /users/counts
    //统计喜欢数 被喜欢数  互相喜欢数
    @GetMapping("/counts")
    public ResponseEntity getCounts(){
        Map<String ,Integer> count = userInfoService.getCounts();
        return ResponseEntity.ok(count);
    }
}
