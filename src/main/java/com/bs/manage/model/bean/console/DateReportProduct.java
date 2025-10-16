package com.bs.manage.model.bean.console;

import com.bs.manage.model.bean.common.CommonModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.Alias;

import java.math.BigDecimal;

/**
 * 2020/3/4 11:31
 * fzj
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("dateReportProduct")
@SuperBuilder
@NoArgsConstructor
public class DateReportProduct extends CommonModel {

    private static final long serialVersionUID = 3629945478568651812L;

    private Long date_report_id;    //日报id
    private Integer selected;       //是否选择 0-否 1-是
    private Long product_id;        //产品id
    private String product_name;    //产品名称
    private BigDecimal amount;      //卖出金额(万元) 2位小数
}
