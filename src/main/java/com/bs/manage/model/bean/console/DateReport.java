package com.bs.manage.model.bean.console;

import com.bs.manage.model.bean.common.CommonModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.Alias;

import java.util.List;

/**
 * 2020/3/4 11:31
 * fzj
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("dateReport")
@SuperBuilder
@NoArgsConstructor
public class DateReport extends CommonModel {

    private static final long serialVersionUID = 3629945478568651812L;

    private Integer date;                 //日期 yyyyMMdd 8位数字
    private Long user_id;                 //提交日报的账号id
    private String user_name;             //提交日报的账号姓名
    private Long team_id;                 //团队id
    private String team_name;             //团队名称
    @NotNull
    private Long customer_id;             //客户id
    private Long customer_category_id;    //客户一级属性id
    private String customer_name;         //客户名称
    private String customer_gradation;    //客户分层 S T Y C
    private Integer activeness;           //活跃度 0-新客户 1-活跃 2-不活跃 3-休眠
    private String purpose;               //拜访目的
    private Integer visit_result;         //拜访结果 1-有成交 2-有意向 3-无意向 4-其他
    private String visit_result_label;    //拜访结果标签
    private String visit_remark;          //拜访备注
    private Integer after_action;         //后续行动  1-开单 2-继续拜访 3-不再拜访 4-其他
    private String after_action_label;    //后续行动标签
    private String action_remark;         //行动备注

    private List<DateReportProduct> reportProducts; //日报中卖出的产品

    @JsonIgnore
    private Boolean ever_buy = false;      //曾经买过
}
