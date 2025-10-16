package com.bs.manage.model.bean.customer;

import com.bs.manage.model.bean.common.CommonDeleteModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.Alias;

/**
 * 2020/2/26 10:13
 * fzj
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("productCategory")
@SuperBuilder
@NoArgsConstructor
public class ProductCategory extends CommonDeleteModel {

    private static final long serialVersionUID = 770433049785391924L;

    private String name;        //主营产品类目名称

}
