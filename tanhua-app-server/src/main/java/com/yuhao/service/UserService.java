package com.yuhao.service;

import com.yuhao.VO.ErrorResult;
import com.yuhao.bean.User;
import com.yuhao.common.utils.Constants;
import com.yuhao.common.utils.JwtUtils;
import com.yuhao.dubbo.api.UserApi;
import com.yuhao.exception.BuinessException;
import com.yuhao.tanhua.autoconfig.template.HuanXinTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserService {

    @Autowired
    private MailService mailService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @DubboReference
    UserApi userApi;

    @Autowired
    HuanXinTemplate huanXinTemplate;

    public void sendMsg(String phone){
        String code = "123456";
        //String code = RandomStringUtils.randomNumeric(6);
//        mailService.sendSimpleMail("yuhao_work1@163.com",phone,
//                "探花交友的验证码", "[探花交友]您的验证码是" + code + ",有效期5分钟,请妥善保管。");
        log.info("验证码是{}", code);
        redisTemplate.opsForValue().set("CHECK_CODE_" + phone, code, 5, TimeUnit.MINUTES);
    }


     //校验验证码
    public Map loginVerification(String phone, String code){
        //1.从redis中获取验证码
        String codeInRedis = redisTemplate.opsForValue().get("CHECK_CODE_" + phone);
        //2.校验验证码 是否和输入的一致
        if (StringUtils.isEmpty(codeInRedis) || !code.equals(codeInRedis)){
            //验证码不存在或者错误
            throw new BuinessException(ErrorResult.loginError());
        }else
        //3. 校验完成 删除redis的验证码
        redisTemplate.delete(phone);
        //4. 通过手机号查询用户
        User user = userApi.findByMobile(phone);
        //5. 用户不存在则创建用户放入数据库

        boolean isNew = false;
        if (user == null){
            user = new User();
            //放入数据库
            user.setMobile(phone);
            user.setPassword(DigestUtils.md5Hex("123456"));
            Long userId = userApi.save(user);
            user.setId(userId);
            isNew = true;

            String hxUser = "hx" + user.getId();
            Boolean isCreated = huanXinTemplate.createUser(hxUser, Constants.INIT_PASSWORD);///123456
            if (isCreated){
                user.setHxUser(hxUser);
                user.setHxPassword(Constants.INIT_PASSWORD);
                userApi.save(user);
            }
        }
        //6. 通过JWT生成token
        Map<String,Object> tokenMap = new HashMap<>();
        tokenMap.put("id", user.getId());
        tokenMap.put("mobile", phone);
        String token = JwtUtils.getToken(tokenMap);
        //7. 构造返回值
        HashMap<Object, Object> returnMap = new HashMap<>();
        returnMap.put("token",token);
        returnMap.put("isNew",isNew);
        return returnMap;
    }
}
