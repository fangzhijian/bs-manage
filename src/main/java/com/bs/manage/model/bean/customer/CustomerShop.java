package com.bs.manage.model.bean.customer;

import com.bs.manage.model.bean.common.CommonDeleteModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.Alias;

import java.math.BigDecimal;
import java.util.List;

/**
 * 2020/7/24 10:27
 * fzj
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("customerShop")
@SuperBuilder
@NoArgsConstructor
public class CustomerShop extends CommonDeleteModel {

    private static final long serialVersionUID = -1054561933701038964L;

    private String name;                                 //客户名称
    private String unique_id;                            //唯一识别(店铺id)
    private Long customer_category_id;                   //客户属性 一级
    private Long customer_category_id2;                  //客户属性 二级
    private Long customer_category_id3;                  //客户属性 三级
    private Long customer_level_id;                      //淘宝客户等级
    private String shop_url;                             //店铺网址
    private String province;                             //省
    private String city;                                 //城市
    private String business_address;                     //办公地址
    private Long customer_product_category_id;           //主营产品类目
    private Long customer_product_nation_id;             //主营产品国籍
    private BigDecimal trade_month;                      //客户规模(月度生意量,万元)
    private String customer_gradation;                   //客户分层 S T Y C
    private Integer trade_type;                          //贸易类型 1一般贸易 2-跨境 3-跨境加一般贸易
    private String parent_unique_id;                     //上级唯一识别

    private String mobile;                          //手机号
    private String customer_category_name;          //客户属性名称 一级
    private String customer_category_name2;         //客户属性名称 二级
    private String customer_category_name3;         //客户属性名称 三级
    private String customer_level_name;             //淘宝客户等级名称
    private String customer_product_category_name;  //主营产品类目
    private String customer_product_nation_name;    //主营产品国籍
    private String activeness_label;                //活跃度说明
    private String trade_type_label;                //贸易类型说明
    private List<CustomerShopContact> customer_shop_contact; //联系人
    private List<CustomerShopAuth> customer_authList;    //客户授权列表

}
