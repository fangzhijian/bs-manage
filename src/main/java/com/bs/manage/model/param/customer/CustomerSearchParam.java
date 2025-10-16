package com.bs.manage.model.param.customer;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 2020/2/28 17:25
 * fzj
 */
@Data
public class CustomerSearchParam implements Serializable {

    private static final long serialVersionUID = 1158395323843992431L;

    @NotNull
    private Integer limit;                               //每页条数
    @NotNull
    private Integer offset;                              //第几条开始
    private String keyword;                              //关键字搜索客户名称或唯一识别
    private String categoryIds2;                         //客户二级属性
    private String customer_level_ids;                   //淘宝客户等级
    private String customer_product_category_ids;        //主营产品类目id2
    private String customer_product_nation_ids;          //主营产品国籍id
    private String customer_gradations;                  //客户分层 S T Y C,逗号分隔
    private Long user_tag_id;                            //账号标签id
    private Integer shop_num;                            //店铺数
    private String province;                             //省
    private String city;                                 //城市


    private List<Long> process_user_ids;                 //下级所有跟进人id
    private Long process_user_id;                        //跟进人id
    private String create_start;                         //创建时间开始,yyyy-MM-dd HH:mm:ss
    private String create_end;                           //创建时间结束,yyyy-MM-dd HH:mm:ss
    private String mobile;                               //联系人电话
    private Long team_id;                                //团队id
    private BigDecimal amount_start;                     //金额开始区间
    private BigDecimal amount_end;                       //金额结束区间

}
