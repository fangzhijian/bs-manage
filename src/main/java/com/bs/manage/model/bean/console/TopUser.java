package com.bs.manage.model.bean.console;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 2020/4/29 11:48
 * fzj
 */
@Data
@Accessors(chain = true)
public class TopUser implements Serializable {

    private static final long serialVersionUID = -2984676096437476686L;

    private Long id;                                  //账号id
    private String name;                              //账号名称
    private Integer order;                            //排序
    private Integer order_diff;                       //排序差值
    private BigDecimal amount = BigDecimal.ZERO;      //销售金额
    private Integer deal_num = 0;                     //成交数
    private Integer visit_num = 0;                    //拜访数
    private BigDecimal deal_ratio = BigDecimal.ZERO;  //成交率
}
