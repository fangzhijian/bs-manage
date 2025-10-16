package com.bs.manage.model.bean.kpi;

import com.bs.manage.model.bean.common.CommonModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.Alias;
import java.util.List;


/**
 * 2020/6/15 16:24
 * fzj
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("kpiMain")
@SuperBuilder
@NoArgsConstructor
public class KpiMain extends CommonModel {

    private static final long serialVersionUID = -739322608563735908L;

    private Long user_id;               //账号id
    private Integer month;              //月份yyyyMM
    private Integer status;             //状态
    private String status_extra_msg;    //状态额外信息
    private Integer has_oneself;        //是否自评 0-否 1-是


    private String username;            //账号姓名
    private List<Long> userIds;         //账号id列表
    private Long parent_id;             //上级id
    private List<KpiContent> contents;  //考核详情列表
}
