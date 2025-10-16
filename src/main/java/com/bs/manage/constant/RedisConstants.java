package com.bs.manage.constant;

/**
 * 2020/3/29 9:05
 * fzj
 */
public interface RedisConstants {

    String USER_CACHE = "cache:user:%s";                    //用户缓存
    String USER_SUBORDINATE_ALL = "subordinate_all";        //所有下属成员
    String USER_SUBORDINATE_SALE = "subordinate_sale";      //所有下属销售成员
    String USER_TAG = "userTag";                            //客户标签
    String CONFIGURE = "configure";                         //配置
    String TEAM = "team";                                   //团队
    String CUSTOMER_LEVEL = "customerLevel";                //客户登记
    String CUSTOMER_CATEGORY = "customerCategory";          //客户属性
    String PRODUCT_CATEGORY = "productCategory";            //主营产品类目
    String PRODUCT_NATION = "productNation";                //主营产品国籍
    String PRODUCT = "product";                             //产品
    String PRODUCT_PROJECT = "productProject";              //产品项目
    String PRODUCT_BRAND = "productBrand";                  //产品品牌
    String REGION = "region";                               //地区
    String TOKEN = "token:";                                //token
    String KPI_EMAIL = "kpiEmail";                          //kpi邮件


}
