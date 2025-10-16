package com.bs.manage.model.bean.customer;

import com.bs.manage.model.bean.console.DateReport;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 2020/3/18 11:09
 * fzj
 */
@Data
public class CustomerDateReport implements Serializable {

    private static final long serialVersionUID = 2639174340837893912L;

    private Integer queryType;            //1-未拜访释放 2-未成交释放 3-未复购释放
    private Long customer_id;             //客户id
    private Long customer_category_id;    //客户属性 一级
    private LocalDateTime created_at;     //客户创建时间
    private String customer_gradation;    //客户分层 S T Y C
    private Long process_user_id;         //跟进人id
    private Integer activeness;           //活跃度 0-新客户 1-活跃 2-不活跃 3-休眠
    private List<DateReport> dateReports; //客户的日报
}
