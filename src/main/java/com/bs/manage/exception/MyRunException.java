package com.bs.manage.exception;

import com.bs.manage.code.PubCode;

/**
 * 2020/2/25 13:23
 * fzj
 */
public class MyRunException extends RuntimeException{


    private static final long serialVersionUID = -877016955441188898L;

    /**
     * 错误代码
     */
    private int code;
    /**
     * 错误信息
     */
    private String message;

    public MyRunException(String message){
        super(message);
        this.code = PubCode.BUSINESS_ERROR.code();
        this.message = message;
    }

    public MyRunException(int code, String message){
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
