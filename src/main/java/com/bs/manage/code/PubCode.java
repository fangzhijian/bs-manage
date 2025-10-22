package com.bs.manage.code;

/**
 * 2020/1/14 17:31
 * fzj
 */
public enum PubCode {

    SUCCESS(0, "ok"),

    SYSTEM_ERROR(1, "网络开了个小差"),

    TIME_OUT(2, "请求超时"),

    PARAM_MISSING(3),

    PARAM_ERROR(4),

    /**
     * 先所有业务逻辑上的异常都用这个码吧,以后有需要再区分
     */
    BUSINESS_ERROR(5),

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
    private int code;
    /**
     * 错误信息
     */
    private String message;

    /**
     * @return 返回错误码
     */
    public int code() {
        return code;
    }

    /**
     * @return 返回错误信息
     */
    public String message() {
        return message;
    }

    /**
     * @param code    错误码
     * @param message 错误信息
     */
    PubCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * @param code 错误码
     */
    PubCode(int code) {
        this.code = code;
    }
}
