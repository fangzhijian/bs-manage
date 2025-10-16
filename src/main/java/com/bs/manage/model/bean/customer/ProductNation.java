package com.bs.manage.model.bean.customer;

import com.bs.manage.model.bean.common.CommonDeleteModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.Alias;

/**
 * 2020/2/26 10:14
 * fzj
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("productNation")
@SuperBuilder
@NoArgsConstructor
public class ProductNation extends CommonDeleteModel {

    private static final long serialVersionUID = -1824041888614319145L;

        private String name;        //主营产品国籍名称

}
