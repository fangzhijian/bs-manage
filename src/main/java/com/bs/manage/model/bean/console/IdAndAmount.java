package com.bs.manage.model.bean.console;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 2020/3/16 15:11
 * fzj
 */
@Data
@Alias("idAndAmount")
@Accessors(chain = true)
public class IdAndAmount implements Serializable {

    private static final long serialVersionUID = -5857380585133999189L;

    private Long id;                   //id
    private BigDecimal amount;         //金额
    private Integer count;             //数量
    private Integer activeness;        //活跃度 0-新客户 1-活跃 2-不活跃 3-休眠
    private Long team_id;              //团队id
    private String customer_gradation; //客户分层
    private Long customer_category_id; //客户一级属性id

}
