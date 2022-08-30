package com.yuhao.exception;

import com.yuhao.VO.ErrorResult;
import lombok.Data;

//自定义异常实体类
@Data
public class BuinessException extends RuntimeException{
    //自定义返回异常属性
    private ErrorResult errorResult;

    public BuinessException(ErrorResult errorResult) {
        //这里的构造方法 传入了错误信息
        //public RuntimeException(String message) {
        //        super(message);
        super(errorResult.getErrMessage());
        this.errorResult = errorResult;
    }
}
