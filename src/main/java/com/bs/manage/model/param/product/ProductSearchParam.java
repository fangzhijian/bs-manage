package com.bs.manage.model.param.product;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 2020/3/2 10:50
 * fzj
 */
@Data
public class ProductSearchParam implements Serializable {

    private static final long serialVersionUID = 5278167847426036999L;

    @NotNull
    private Integer limit;              //每页条数
    @NotNull
    private Integer offset;             //第几条开始
    private String name;                //产品名称
    private Long product_brand_id;      //产品品牌id
    private Long product_project_id;    //产品项目id

}
