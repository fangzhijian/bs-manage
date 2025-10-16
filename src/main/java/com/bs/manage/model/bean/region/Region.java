package com.bs.manage.model.bean.region;

import com.bs.manage.model.bean.common.LabelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.Alias;

/**
 * 2020/2/28 12:57
 * fzj
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("region")
@SuperBuilder
@NoArgsConstructor
public class Region extends LabelModel {

    private static final long serialVersionUID = -8208614007335095967L;

    private Integer type;       //0国家，1省份，2城市，3区县，4街道/镇
    private Integer is_oversea; //1 国外 0国内
    private String parent_name; //上级名称
}
