package com.bs.manage.model.bean.customer;

import com.bs.manage.model.bean.common.CommonDeleteModel;
import com.bs.manage.model.bean.common.CommonModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.Alias;


/**
 * 2020/2/19 15:14
 * fzj
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("customerContact")
@SuperBuilder
@NoArgsConstructor
public class CustomerContact extends CommonDeleteModel {

    private static final long serialVersionUID = 2846668779698058923L;

    private Long customer_id;                   //客户id
    private String person;                      //联系人
    private String person_title;                //联系人职位
    private String mobile;                      //手机号
    private String wangwang;                    //旺旺
    private String wx;                          //微信ID
}
