package com.bs.manage.model.bean.customer;

import com.bs.manage.model.bean.common.LabelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.Alias;

/**
 * 2020/2/26 11:41
 * fzj
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("customerCategory")
@SuperBuilder
@NoArgsConstructor
public class CustomerCategory extends LabelModel {

    private static final long serialVersionUID = -8647235747947391925L;

    public static final Long RETAILER = 1L;        //零售id
    public static final Long DEALER = 2L;          //经销商id
    public static final Long TAO_BAO = 4L;         //淘宝id


}
