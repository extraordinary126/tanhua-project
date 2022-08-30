package com.yuhao.controller;

import com.yuhao.bean.UserInfo;
import com.yuhao.interceptor.UserThreadLocalHolder;
import com.yuhao.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 保存用户信息
     * /user/loginReginfo  POST
     * Headers
     * Content-Type	application/json	是
     * Authorization	token	是
     * <p>
     * 名称	类型	是否必须	默认值	备注	其他信息
     * gender	string  必须  性别 man woman
     * nickname	string必须昵称
     * birthday	string必须  生日 年月日
     * city	string 必须 城市
     * header	string 非必须 用户头像
     */
    @PostMapping("/loginReginfo")
    public ResponseEntity loginReginfo(@RequestBody UserInfo userInfo,
                                       @RequestHeader("Authorization") String token) {
      /*  //1.判断token是否合法
        boolean verifyToken = JwtUtils.verifyToken(token);
        if (!verifyToken) {
            //不合法 重新登录
            return ResponseEntity.status(401).body(null);
        }*/
        //2.保存信息页 userinfo 和 user不是一张表 用户没有ID 从token里拿
        /*Claims claims = JwtUtils.getClaims(token);
        Integer id = (Integer) claims.get("id");
        userInfo.setId(Long.valueOf(id));*/
        userInfo.setId(UserThreadLocalHolder.getId());
        //3.调用service
        userInfoService.save(userInfo);

        return ResponseEntity.ok(null);
    }

    ///user/loginReginfo/head
    @PostMapping("/loginReginfo/head")
    public ResponseEntity uploadHead(@RequestBody MultipartFile headPhoto,
                                     @RequestHeader("Authorization") String token){
        //1.判断token是否合法
        /*boolean verifyToken = JwtUtils.verifyToken(token);
        if (!verifyToken) {
            //不合法 重新登录
            return ResponseEntity.status(401).body(null);
        }*/
        //2.保存信息页 userinfo 和 user不是一张表 用户没有ID 从token里拿
        /*Claims claims = JwtUtils.getClaims(token);
        Integer id = (Integer) claims.get("id");*/
        Long id = UserThreadLocalHolder.getId();
        //3. 调用service 上传图片
        userInfoService.updateHead(headPhoto,id);
        return ResponseEntity.ok(null);
    }
}
