package com.springboot.demo.exception;

import com.springboot.demo.code.PubCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 自定义异常类,一般用于主动抛出、捕捉
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BusinessException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 1L;


    /**
     * 错误代码
     */
    private int code;
    /**
     * 错误信息
     */
    private String message;

    public BusinessException(String message){
        super(message);
        this.code = PubCode.BUSINESS_ERROR.code;
        this.message = message;
    }

    public BusinessException(int code, String message){
        super(message);
        this.code = code;
        this.message = message;
    }



}
