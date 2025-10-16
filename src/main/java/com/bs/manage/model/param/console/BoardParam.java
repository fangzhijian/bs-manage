package com.bs.manage.model.param.console;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * 2020/3/17 16:14
 * fzj
 */
@Data
@SuperBuilder
@NoArgsConstructor
public class BoardParam implements Serializable {
    private static final long serialVersionUID = 8841815774657861261L;

    private Integer type;       //查询类型 1-团队 2-项目 3-品牌 4-客户
    private Integer month;      //查询月份
    private Integer date;       //查询日期
    private Long user_id;       //账号id
    private Integer monthEnd;   //查询月份最大天数,默认31如果是当月为查询前1天
    private Integer order_type; //排行类型 1-销售额 2-成交数 3-成交率

    private Integer prevMoth;   //查询月的上一个月
    private Long team_id ;      //团队id
    private Integer role;       //账号角色
    private List<Long> userIds; //账号id集合
}
