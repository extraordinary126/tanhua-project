package com.yuhao.interceptor;

import com.yuhao.bean.User;

/**
 * 工具类 使用ThreadLocal存储数据
 *
 */
public class UserThreadLocalHolder {


    private static ThreadLocal<User> threadLocal = new ThreadLocal<>();

    //将用户对象 存入ThreadLocal
    public static void setUser(User user){
        threadLocal.set(user);
    }

    //从当前线程获取对象
    public static User getUser(){
        return threadLocal.get();
    }

    //获取用户对象的id
    public static Long getId(){
        return threadLocal.get().getId();
    }

    //获取用户对象的手机号
    public static String getMobile(){
        return threadLocal.get().getMobile();
    }

    public static void cleanThreadLocal(){
        threadLocal.remove();
    }
}
