package com.bs.manage.model.bean.customer;

import com.bs.manage.model.bean.common.CommonDeleteModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.Alias;

/**
 * 2020/7/24 10:34
 * fzj
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("customerShopContact")
@SuperBuilder
@NoArgsConstructor
public class CustomerShopContact extends CommonDeleteModel {

    private static final long serialVersionUID = 3538201611341553335L;

    private Long customer_shop_id;           //客户店铺id
    private String person;                   //联系人
    private String person_title;             //联系人职位
    private String mobile;                   //手机号
    private String wangwang;                 //旺旺账号
    private String wx;                       //微信id
}
