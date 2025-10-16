package com.bs.manage.model.param.configures;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 2020/4/26 14:40
 * fzj
 */
@Data
public class ResultGoalInfo implements Serializable {

    private static final long serialVersionUID = 3529697315505798181L;

    private Long id;                             //id
    private Long team_id;                        //团队id
    private String team_name;                    //团队名称
    private Long customer_category_id;           //客户一级属性id
    private String customer_category_name;       //客户一级属性名称
    private Long product_project_id;             //产品项目id
    private String product_project_name;         //产品项目名称
    private Integer goal_month;                  //目标月份yyyyMM
    private BigDecimal amount_s;                 //客户分层S-销售额目标
    private BigDecimal amount_t;                 //客户分层T-销售额目标
    private BigDecimal amount_y;                 //客户分层Y-销售额目标
    private BigDecimal amount_c;                 //客户分层C-销售额目标
    private BigDecimal user_total;               //个人目标总金额
}
