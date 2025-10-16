package com.bs.manage.model.json;

import com.bs.manage.code.PubCode;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 2020/1/14 17:25
 * fzj
 */
@Data
@Accessors(chain = true)
public class ResponseJson implements Serializable {

    private static final long serialVersionUID = 8542404474259971837L;

    public Object data;     //返回数据
    public int code;        //状态码
    public String msg;      //状态信息


    public static ResponseJson fail(String msg) {
        return new ResponseJson().setCode(PubCode.BUSINESS_ERROR.code()).setMsg(msg);
    }

    public static ResponseJson fail(int code, String msg) {
        return new ResponseJson().setCode(code).setMsg(msg);
    }

    public static ResponseJson fail(int code, String msg, Object data) {
        return new ResponseJson().setCode(code).setMsg(msg).setData(data);
    }

    public static ResponseJson success() {
        return new ResponseJson().setCode(PubCode.SUCCESS.code()).setMsg(PubCode.SUCCESS.message());
    }

    public static ResponseJson success(Object data) {
        return new ResponseJson().setCode(PubCode.SUCCESS.code()).setMsg(PubCode.SUCCESS.message()).setData(data);
    }

}
