package com.bs.manage.model.bean.product;

import com.bs.manage.model.bean.common.CommonDeleteModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.Alias;

/**
 * 2020/3/2 10:46
 * fzj
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("productBrand")
@SuperBuilder
@NoArgsConstructor
public class ProductBrand extends CommonDeleteModel {

    private static final long serialVersionUID = 1822822183754147432L;

    private String name;  //产品品牌名称
}
