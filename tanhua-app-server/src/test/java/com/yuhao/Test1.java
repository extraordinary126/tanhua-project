package com.yuhao;

import io.jsonwebtoken.*;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Test1 {

    @Test
    public void test1(){
        //生成token
        //1. 准备数据
        Map map = new HashMap<>();
        map.put("id",1);
        map.put("mobile","13495026091");
        long now = System.currentTimeMillis();
        //2. 使用工具类生成token
        String jwt = Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, "secretKey")  //设置算法和秘钥
                .setClaims(map)             //存入数据
                .setExpiration(new Date(now + 5000))    //设置失效时间
                .compact();
        System.out.println(jwt);
    }
    //json不合法 后会抛出异常
    //io.jsonwebtoken.SignatureException:
    //json失效后解析会抛出异常
    //io.jsonwebtoken.ExpiredJwtException: JWT expired at 2022-08-29T09:14:29Z. Current time: 2022-08-29T09:14:45Z,
    @Test
    public void test2(){
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJtb2JpbGUiOiIxMzQ5NTAyNjA5MSIsImlkIjoxLCJleHAiOjE2NjE3MzU2Njl9.F3HVKvDiS71bgx-TSRI0Yxy4VZcaC5hurVokOQQKF8_vTvKfSCk7WmAocnC5THdGbVFfKya8OZVCaR_xBv_Umg";
        try {
            Claims claims = Jwts.parser().setSigningKey("secretKey")
                    .parseClaimsJws(token)
                    .getBody();
            System.out.println(claims.get("id"));
            System.out.println(claims.get("mobile"));
            System.out.println(claims  );
        }catch (SignatureException e){
            e.printStackTrace();
            System.out.println("token过期");
        }catch (ExpiredJwtException e){
            e.printStackTrace();
            System.out.println("token不合法");
        }

    }
}
