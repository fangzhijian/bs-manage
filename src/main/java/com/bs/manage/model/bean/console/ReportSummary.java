package com.bs.manage.model.bean.console;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 2020/3/24 10:02
 * fzj
 */
@Data
@Alias("reportSummary")
@Accessors(chain = true)
public class ReportSummary implements Serializable {

    private static final long serialVersionUID = -3835293100283298421L;

    private String label;                   //标签
    private Integer visit = 0;              //拜访客户数
    private BigDecimal average_sale;        //平均分销数
    private Integer result_deal = 0;        //成交客户数
    private Integer result_mind = 0;        //有意客户数
    private Integer result_no_mind = 0;     //无意客户数
    private Integer result_other = 0;       //其他结果客户数
    private Integer action_deal = 0;        //开单客户数
    private Integer action_continue = 0;    //继续拜访客户数
    private Integer action_not = 0;         //不再拜访客户数
    private Integer action_other = 0;       //其他行动客户数
    private List<ReportSummary> child;      //子节点,字段同上

    @JsonIgnore
    private Long customer_category_id;      //客户一级属性id
    @JsonIgnore
    private String customer_gradation;      //客户分层
    @JsonIgnore
    private Long team_id;                   //团队id
    @JsonIgnore
    private Integer product_num = 0;        //日报中的产品数

}
