package com.springboot.demo.code;

/**
 * 2020/1/14 17:31
 * fzj
 */
public enum PubCode {

    SUCCESS(0, "ok"),

    SYSTEM_ERROR(1, "网络开了个小差"),

    TIME_OUT(2, "请求超时"),

    PARAM_MISSING(3,"缺少参数"),

    PARAM_ERROR(4,"参数错误"),

    /**
     * 先所有业务逻辑上的异常都用这个码吧,以后有需要再区分
     */
    BUSINESS_ERROR(5,"业务异常"),

    LOGIN_ERROR(6, "账号未登录或已过期,请重新登录"),

    LIMITED_AUTHORITY(7, "权限不足"),

    USER_STATUS(8, "账号ID%s已停用或离职"),

    KPI_NOT_MAKE(11,"绩效未制定"),

    P2006_REPEAT_CLICK(2006,"操作频率过快,请稍后再试"),

    P2007_RESOURCE_CLICK(2007,"当前人数过多,稍后再试试看"),
    ;

    public static final int SUCCESS_CODE = 0;

    /**
     * 错误码
     */
    public final int code;
    /**
     * 错误信息
     */
    public final String message;

    /**
     * @param code    错误码
     * @param message 错误信息
     */
    PubCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
