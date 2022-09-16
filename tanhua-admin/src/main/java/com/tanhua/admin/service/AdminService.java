package com.tanhua.admin.service;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tanhua.admin.exception.BusinessException;
import com.tanhua.admin.interceptor.AdminHolder;
import com.tanhua.admin.mapper.AdminMapper;
import com.yuhao.VO.AdminVo;
import com.yuhao.bean.Admin;
import com.yuhao.common.utils.Constants;
import com.yuhao.common.utils.JwtUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdminService {

    @Resource
    private AdminMapper adminMapper;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    //校验验证码
    public Map login(Map map) {
        //1.从map集合中获取参数=
        String username = (String) map.get("username");
        String password = (String) map.get("password");
        String verificationCode = (String) map.get("verificationCode");
        String uuid = (String) map.get("uuid");
        //2.校验验证码是否正确
        String key = Constants.CAP_CODE + uuid;
        String value = redisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(value) || !value.equals(verificationCode)){

            throw new BusinessException("验证码错误");
        }
        redisTemplate.delete(key);
        //3.根据用户名查询admin
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getUsername, username);
        Admin admin = adminMapper.selectOne(queryWrapper);
        //4.判断非空 密码是否一致
        password = SecureUtil.md5(password);
        if (admin == null || !(admin.getPassword().equals(password))){
            throw new BusinessException("用户名或者密码不正确!");
        }
        //5.构造token
        Map<String,Object> tokenMap = new HashMap();
        tokenMap.put("username",username);
        tokenMap.put("id",admin.getId());
        String token = JwtUtils.getToken(tokenMap);
        //6.返回
        HashMap<String,String> retMap = new HashMap();
        retMap.put("token",token);
        return retMap;
    }

    //获取用户信息
    public AdminVo profile() {
        //拦截器已经将token解析并且放入threadLocal
        Long userId = AdminHolder.getUserId();
        Admin admin = adminMapper.selectById(userId);
        return AdminVo.init(admin);
    }
}
