package com.bs.manage.exception;

import com.bs.manage.code.PubCode;

/**
 * 自定义异常类,一般用于主动抛出、捕捉
 */
public class BusinessException extends Exception{


    private static final long serialVersionUID = -4013608389132470062L;

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
        this.code = PubCode.BUSINESS_ERROR.code();
        this.message = message;
    }

    public BusinessException(int code, String message){
        super(message);
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
