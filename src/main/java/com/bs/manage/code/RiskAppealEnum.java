package com.bs.manage.code;

import lombok.Data;

/**
 *
 * @author fangzhijian
 * @date 2025/9/19 09:40
 */
public enum RiskAppealEnum {

    REVIEW_WAIT(1,"待审核"),
    REVIEW_ING(2,"审核中");

    public final Integer code;
    public final String desc;

    RiskAppealEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
