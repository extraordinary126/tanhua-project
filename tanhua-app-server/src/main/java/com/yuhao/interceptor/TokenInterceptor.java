package com.yuhao.interceptor;

import com.yuhao.bean.User;
import com.yuhao.common.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TokenInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.获取请求头
        String token = request.getHeader("Authorization");
//        //2.使用工具类 判断token是否有效
//        boolean verifyToken = JwtUtils.verifyToken(token);
//        if (!verifyToken) {
//            System.out.println("请求已经被拦截");
//            //不合法
//            response.setStatus(401);
//            //拦截
//            return false;
//        }
        //token校验完成 解析token 构造User对象存入ThreadLocal
        User user = new User();
        Claims claims = JwtUtils.getClaims(token);
        Integer id = (Integer) claims.get("id");
        user.setId(Long.valueOf(id));
        String mobile = (String) claims.get("mobile");
        user.setMobile(mobile);
        UserThreadLocalHolder.setUser(user);

        //token正常 放行
        return true;
    }

    //清除ThreadLocal
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserThreadLocalHolder.cleanThreadLocal();
    }
}
