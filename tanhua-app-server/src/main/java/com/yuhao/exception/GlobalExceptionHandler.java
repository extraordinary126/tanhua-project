package com.yuhao.exception;

import com.yuhao.VO.ErrorResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 自定义统一异常处理
 *  1.通过注解 声明这是异常处理类 @ControllerAdvice
 *  2.编写方法,在方法内部处理异常,构造响应数据
 *  3.方法上加上注解  指定此方法可以处理的类型 @ExceptionHandler(Exception.class);
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BuinessException.class)
    public ResponseEntity handlerException(BuinessException be){
        be.printStackTrace();
        ErrorResult errorResult = be.getErrorResult();
        return ResponseEntity.status(500).body(errorResult);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handlerException1(BuinessException be){
        be.printStackTrace();
        return ResponseEntity.status(500).body(ErrorResult.error());
    }
}
