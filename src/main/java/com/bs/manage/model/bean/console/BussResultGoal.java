package com.bs.manage.model.bean.console;

import com.bs.manage.model.bean.common.CommonModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.Alias;

import java.math.BigDecimal;

/**
 * 2020/3/12 11:08
 * fzj
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("bussResultGoal")
@SuperBuilder
@NoArgsConstructor
public class BussResultGoal extends CommonModel {

    private static final long serialVersionUID = 923335753403793127L;

    private Long user_id;                        //账号id
    private Long team_id;                        //团队id
    private Long customer_category_id;           //客户一级属性id
    private Long product_project_id;             //产品项目id
    private Integer goal_month;                  //目标月份yyyyMM
    private BigDecimal amount_s;                 //客户分层S-销售额目标
    private BigDecimal amount_t;                 //客户分层T-销售额目标
    private BigDecimal amount_y;                 //客户分层Y-销售额目标
    private BigDecimal amount_c;                 //客户分层C-销售额目标

    private Long product_brand_id;              //产品品牌id
}
