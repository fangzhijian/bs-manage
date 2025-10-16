package com.bs.manage.model.bean.product;

import com.bs.manage.model.bean.common.CommonDeleteModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.Alias;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 2020/3/2 10:43
 * fzj
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("product")
@SuperBuilder
@NoArgsConstructor
public class Product extends CommonDeleteModel {

    private static final long serialVersionUID = 5738218509310508603L;

    @NotBlank
    private String name;                //产品名称
    @NotBlank
    private String barcode;             //产品条码
    private Long product_brand_id;      //产品品牌id
    private String product_brand_name;  //产品品牌名称
    @NotNull
    private Long product_project_id;    //产品项目id
    private String product_project_name;//产品项目名称
    private Integer is_major;           //是否重点项目 0-否 1-是
}
