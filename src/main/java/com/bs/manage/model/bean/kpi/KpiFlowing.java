package com.bs.manage.model.bean.kpi;

import com.bs.manage.model.bean.common.CommonModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.Alias;

/**
 * 2020/6/15 16:26
 * fzj
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("kpiFlowing")
@SuperBuilder
@NoArgsConstructor
public class KpiFlowing extends CommonModel {

    private static final long serialVersionUID = -2414992771723514013L;

    private Long kpi_id;                 //kpi主表id
    private Integer status;              //流程状态
    private String operate_user;         //操作人

    private String status_label;         //状态说明
    private Integer finish_status;       //1-已完成 2-进行中 3-未开始

}
