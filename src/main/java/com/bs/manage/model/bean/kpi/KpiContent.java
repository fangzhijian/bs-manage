package com.bs.manage.model.bean.kpi;

import com.bs.manage.model.bean.common.CommonModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.Alias;

import java.math.BigDecimal;

/**
 * 2020/6/15 16:25
 * fzj
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("kpiContent")
@SuperBuilder
@NoArgsConstructor
public class KpiContent extends CommonModel {

    private static final long serialVersionUID = 3241173746008197188L;

    private Long kpi_id;                   //kpi主表id
    private String kpi_type;               //考核维度
    private String kpi_label;              //kpi指标
    private String kpi_content;            //指标说明
    private String kpi_goal;               //衡量标准
    private String oneself_deal;           //自评达成
    private String actual_deal;            //实际达成
    private Integer weight;                //权重百分比
    private BigDecimal oneself_score;      //自评得分
    private BigDecimal superior_score;     //上级评分

}
