package com.bs.manage.model.param.kpi;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 2020/6/16 16:21
 * fzj
 */
@Data
@SuperBuilder
@NoArgsConstructor
public class KpiListResult implements Serializable {

    private static final long serialVersionUID = -5274004903643997057L;

    private Long id ;                   //ID
    private Long user_id;               //账号id
    private String username;            //姓名
    private Integer month;              //年月份
    private Integer status;             //状态
    private String status_label;        //状态说明
    private String status_extra_msg;    //状态额外信息
    private List<Integer> buttons;      //能操作的按钮
    private Long parent_id;             //上级id
    private String parent_name;         //上级姓名
    private Integer has_finish;         //是否已考核 0-否 1-是
    private BigDecimal oneself_score;   //自评得分
    private BigDecimal superior_score;  //上级评分
    private LocalDateTime created_at;   //创建时间
}
