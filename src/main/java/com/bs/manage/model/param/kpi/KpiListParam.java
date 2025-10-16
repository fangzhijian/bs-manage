package com.bs.manage.model.param.kpi;

import com.bs.manage.model.bean.account.User;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 2020/6/16 16:49
 * fzj
 */
@Data
public class KpiListParam implements Serializable {

    private static final long serialVersionUID = -58598056345902736L;

    @NotNull
    private Integer limit;
    @NotNull
    private Integer offset;
    private Integer month;              //月份yyyyMM
    private Integer status;             //状态
    private Integer has_finish;         //是否已考核 0-否 1-是
    private String username;            //账号名称
    private List<User> users;           //账号列表
    private List<Long> userIds;         //账号id列表
    private String ids;                 //勾选的多个id,逗号隔开
}
