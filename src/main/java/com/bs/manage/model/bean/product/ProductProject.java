package com.bs.manage.model.bean.product;

import com.bs.manage.model.bean.common.CommonDeleteModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.Alias;

import jakarta.validation.constraints.NotNull;

/**
 * 2020/3/2 10:46
 * fzj
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("productProject")
@SuperBuilder
@NoArgsConstructor
public class ProductProject extends CommonDeleteModel {

    private static final long serialVersionUID = 2869903549919922915L;
    private String name;                //产品项目名称
    @NotNull
    private Long product_brand_id;      //产品品牌id
    private String product_brand_name;  //产品品牌名称
    private Integer is_major;           //是否重点项目 0-否 1-是
}
