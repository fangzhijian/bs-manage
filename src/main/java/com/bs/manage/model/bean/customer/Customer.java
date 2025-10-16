package com.bs.manage.model.bean.customer;

import com.bs.manage.model.bean.common.CommonDeleteModel;
import com.bs.manage.model.bean.console.DateReport;
import com.bs.manage.model.json.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.Alias;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 2020/2/27 10:10
 * fzj
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("customer")
@SuperBuilder
@NoArgsConstructor
public class Customer extends CommonDeleteModel {

    private static final long serialVersionUID = -6799668545092583680L;

    private String sn;                                   //客户编号
    private String name;                                 //客户名称
    private String unique_id;                            //唯一识别
    private Long customer_category_id;                   //客户属性 一级
    private Long customer_category_id2;                  //客户属性 二级
    private Long customer_category_id3;                  //客户属性 三级
    private Long customer_level_id;                      //淘宝客户等级
    private String shop_url;                             //店铺网址
    private String province;                             //省
    private String city;                                 //城市
    private String business_address;                     //办公地址
    private Long process_user_id;                        //跟进人
    private Long team_id;                                //跟进人的团队id
    private Long customer_product_category_id;           //主营产品类目
    private Long customer_product_nation_id;             //主营产品国籍
    private BigDecimal trade_month;                      //客户规模(月度生意量,万元)
    private String customer_gradation;                   //客户分层 S T Y C
    private Integer trade_type;                          //贸易类型 1一般贸易 2-跨境 3-跨境加一般贸易
    private Integer activeness;                          //0-新客户 1-活跃 2-不活跃 3-休眠
    private BigDecimal total_amount;                     //总销售额
    private LocalDateTime near_deal_time;                //最近交易时间
    private LocalDateTime prev_deal_time;                //前一次交易时间
    private LocalDateTime bind_time;                     //绑定时间
    private Integer allocate_again;                      //重复分配次数
    private Integer shop_num;                            //店铺数

    private String mobile;                          //手机号
    private String customer_category_name;          //客户属性名称 一级
    private String customer_category_name2;         //客户属性名称 二级
    private String customer_category_name3;         //客户属性名称 三级
    private String customer_level_name;             //淘宝客户等级名称
    private String customer_product_category_name;  //主营产品类目
    private String customer_product_nation_name;    //主营产品国籍
    private String process_user_name;               //跟进人姓名
    private String activeness_label;                //活跃度说明
    private String trade_type_label;                //贸易类型说明
    private List<CustomerContact> customer_contact; //联系人
    private List<CustomerTagRelation> customer_tags;//客户标签
    private List<CustomerShop> customerShops;       //客户店铺

    private Integer importType;                     //录入类型 1-客户加店铺 2-客户 3-店铺
    private Boolean isUpgrade = false;              //是否是店铺升级客户
    private String parent_unique_id;                //上级唯一识别
    private String insertError;                     //插入失败信息
    private Integer insertRowNum;                   //插入的行号
    private Page<DateReport> dateReportPage;        //日报详情
}
