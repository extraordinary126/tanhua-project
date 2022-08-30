package com.yuhao.controller;

import com.yuhao.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class LoginController {

    @Autowired
    private  UserService userService;

    /**
     * 用户登录 获取验证码
     * /user/login
     * phone	string
     * 必须       手机号
     *
     */
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Map map){
        String phone = (String) map.get("phone");
        userService.sendMsg(phone);
        //ResponseEntity spring定义的响应实体对象
        //return ResponseEntity.status(500).body("出错了");
        return ResponseEntity.ok(null); //正常返回状态码200
    }

    /**
     * 校验登录 /user/loginVerification
     * 名称	            类型	是否必须	默认值	备注	其他信息
     * phone             String	必须     手机号
     * verificationCode  String  必须    传入的验证码
     *
     */
    /**
     //返回数据
     名称	类型	是否必须	默认值	备注	其他信息
     token	string  必须   jwt-token字符串
     isNew	string  必须      是否新用户  false表示不是新用户
     */
    @PostMapping("/loginVerification")
    public ResponseEntity loginVerification(@RequestBody Map map){
            //调用 map集合获得请求参数
            String phone = (String) map.get("phone");
            String code = (String) map.get("verificationCode");
            //调用 UserService完成用户登录
            Map returnMap = userService.loginVerification(phone, code);
            //构造返回
            return ResponseEntity.ok(returnMap);

    }

}
