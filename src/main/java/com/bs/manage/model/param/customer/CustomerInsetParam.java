package com.bs.manage.model.param.customer;

import com.bs.manage.model.bean.customer.CustomerContact;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 2020/2/19 13:48
 * fzj
 */
@Data
public class CustomerInsetParam implements Serializable {

    private static final long serialVersionUID = -4879548579294897883L;

    private String name;                        //客户名称
    private String unique_id;                   //唯一识别(店铺id)
    private Long customer_category_id;          //客户属性 一级
    private Long customer_category_id2;         //客户属性 二级
    private Long customer_category_id3;         //客户属性 三级
    private Long customer_level_id;             //淘宝客户等级
    private String shop_url;                    //店铺网址
    private Long  province_id ;                 //省id
    private Long  city_id ;                     //城市id
    private Long customer_product_category_id;  //主营类目
    private Long customer_product_nation_id;    //主营产品国籍
    private BigDecimal month_amount;            //客户规模(月度生意量,万元)
    private Integer trade_type;                 //贸易类型 1-跨境 2-一般贸易 3-跨境加一般贸易
    private List<CustomerContact> customerContact;              //联系人
}
