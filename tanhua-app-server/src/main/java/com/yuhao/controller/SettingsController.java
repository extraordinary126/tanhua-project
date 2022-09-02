package com.yuhao.controller;


import com.yuhao.VO.PageResult;
import com.yuhao.VO.SettingsVo;
import com.yuhao.bean.Settings;
import com.yuhao.interceptor.UserThreadLocalHolder;
import com.yuhao.service.SettingsService;
import com.yuhao.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class SettingsController {

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private UserService userService;

    //查询通用设置  /users/settings
    @GetMapping("/settings")
    public ResponseEntity<SettingsVo> getSettings(@RequestHeader("Authorization") String token) {

        SettingsVo settingsVo = settingsService.getSettings();
        return ResponseEntity.ok(settingsVo);
    }

    ///users/questions  设置陌生人问题
    @PostMapping("/questions")
    public ResponseEntity setQuestions(@RequestBody Map map, @RequestHeader("Authorization") String token) {
        String content = (String) map.get("content");
        settingsService.setQuestions(content);
        return ResponseEntity.ok(null);
    }
    ///users/notifications/setting 用户通知功能设置
    @PostMapping("/notifications/setting")
    public ResponseEntity setNotification(@RequestHeader("Authorization") String token,
                                          @RequestBody Settings settings){
//        boolean likeNotification = (boolean) map.get("likeNotification");       //喜欢 推送通知
//        boolean pinglunNotification = (boolean) map.get("pinglunNotification"); //评论 推送通知
//        boolean gonggaoNotification = (boolean) map.get("gonggaoNotification"); //公告 推送通知

        settingsService.setNofication(settings);
        return ResponseEntity.ok(null);
    }

    // /users/blacklist  分页查询黑名单里的用户
    @GetMapping("/blacklist")
    public ResponseEntity getBlackList(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int pagesize){
        //分页查询
        PageResult pageResult = settingsService.getBlacklist(page, pagesize);

        return ResponseEntity.ok(pageResult);
    }

    ///users/blacklist/:uid
    @DeleteMapping("/blacklist/{uid}")
    public ResponseEntity cancelBlacklist(@PathVariable("uid") Long uid){
        settingsService.cancelBlacklist(uid);
        return ResponseEntity.ok(null);
    }

    //修改手机号1: 发送短信验证码
    ///users/phone/sendVerificationCode POST
    @PostMapping("/phone/sendVerificationCode")
    public ResponseEntity updatePhoneNumber(){
        String phoneNumber = UserThreadLocalHolder.getMobile();
        userService.sendMsg(phoneNumber);
        return ResponseEntity.ok(null);
    }

    //修改手机号2: 校验验证码
    ///users/phone/checkVerificationCode
    @PostMapping("/phone/checkVerificationCode")
    public ResponseEntity checkVerificationCode(@RequestBody Map map){
        String verificationCode = (String) map.get("verificationCode");
        boolean verification = settingsService.checkVerificationCode(verificationCode);
        Map<String, Boolean> retMap = new HashMap<>();
        retMap.put("verification",verification);
        return ResponseEntity.ok(retMap);
    }

    //修改手机号3: 保存新的手机号
    ///users/phone  POST

    @PostMapping("/phone")
    public ResponseEntity saveNewPhoneNumber(@RequestBody Map map){
        String phone = (String) map.get("phone");
        settingsService.updatePhoneNumber(phone);
        return ResponseEntity.ok(null);
    }

}
