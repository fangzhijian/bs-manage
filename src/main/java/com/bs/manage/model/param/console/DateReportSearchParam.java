package com.bs.manage.model.param.console;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 2020/3/4 11:47
 * fzj
 */
@Data
public class DateReportSearchParam implements Serializable {

    private static final long serialVersionUID = -6727489674624374601L;

    @NotNull
    private Integer limit;                               //每页条数
    @NotNull
    private Integer offset;                              //第几条开始
    private String userName;                             //姓名
    private String customerName;                         //客户名称
    private Integer date;                                //日期 yyyyMMdd 8位数字
    private List<Long> user_ids;                         //账号id
    private Boolean queryByCustomer = false;             //是否是在客户详情中显示
    private Long customer_id;                            //客户id
    private Integer visit_result;                        //拜访结果 1-有成交 2-有意向 3-无意向 4-其他
    private Integer after_action;                        //后续行动  1-开单 2-继续拜访 3-不再拜访 4-其他
}
