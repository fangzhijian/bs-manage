package com.bs.manage.model.bean.customer;

import com.bs.manage.model.bean.common.CommonDeleteModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.Alias;

/**
 * 2020/2/26 10:10
 * fzj
 * 客户等级
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("customerLevel")
@SuperBuilder
@NoArgsConstructor
public class CustomerLevel extends CommonDeleteModel {

    private static final long serialVersionUID = 7423325851971353638L;

    private String name;        //客户等级名称
}
