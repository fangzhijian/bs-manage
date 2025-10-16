package com.bs.manage.model.param.configures;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 2020/4/26 14:20
 * fzj
 */
@Data
public class ResultGoalDisplay implements Serializable {

    private static final long serialVersionUID = 4699722083950396893L;

    private Long user_id;               //账号id
    private String user_name;           //账号名称
    private BigDecimal user_total;      //个人目标总金额
    private List<ResultGoalInfo> infos; //详细信息
}
