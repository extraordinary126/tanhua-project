package com.tanhua.admin.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.tanhua.admin.service.AdminService;
import com.yuhao.VO.AdminVo;
import com.yuhao.common.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/system/users")
public class SystemController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    ///system/users/verification
    //进入页面时,展示验证码图片
    @GetMapping("/verification")
    public void verification(String uuid, HttpServletResponse response) throws IOException {
        //1.生成验证码图片
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(299, 97);
        //2.验证码存入redis
        String code = lineCaptcha.getCode();
        //uuid保证数据唯一性
        redisTemplate.opsForValue().set(Constants.CAP_CODE + uuid, code, Duration.ofMinutes(5));
        //3.输出验证码图片
        lineCaptcha.write(response.getOutputStream());
    }

    /**
     * 用户登录：
     */
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Map map) {
        Map retMap = adminService.login(map);
        return ResponseEntity.ok(retMap);
    }

    /**
     * 获取用户的信息
     */
    @PostMapping("/profile")
    public ResponseEntity profile() {
        AdminVo vo = adminService.profile();
        return ResponseEntity.ok(vo);
    }


}
