package com.springboot.demo.code;

import com.springboot.demo.annotation.CodeAnnotation;

/**
 * 2020/2/18 16:33
 * fzj
 * 用于存放code加code对应的中文说明
 * 要用int long等编译期常量.不能用Integer Long
 */
public class CodeCaption {


    public static final String USER_STATUS = "账号状态";

    @CodeAnnotation(category = USER_STATUS, value = "正常")
    public static final int STATUS_OK = 1;
    @CodeAnnotation(category = USER_STATUS, value = "停用")
    public static final int STATUS_STOP = 2;
    @CodeAnnotation(category = USER_STATUS, value = "离职")
    public static final int STATUS_LEAVE = 3;

    public static final String BOOLEAN = "是否";
    @CodeAnnotation(category = BOOLEAN, value = "否")
    public static final int FALSE = 0;
    @CodeAnnotation(category = BOOLEAN, value = "是")
    public static final int TRUE = 1;

    public static final String ROLE = "角色";
    @CodeAnnotation(category = ROLE, value = "专员")
    public static final int ROLE_SALE = 1;
    @CodeAnnotation(category = ROLE, value = "管理员")
    public static final int ROLE_ADMIN = 2;
    @CodeAnnotation(category = ROLE, value = "推广")
    public static final int ROLE_EXTENSION = 3;
    @CodeAnnotation(category = ROLE, value = "自定义")
    public static final int ROLE_USER_DEFINED = 4;


    public static final String CUSTOMER_CATEGORY = "客户属性";
    @CodeAnnotation(category = CUSTOMER_CATEGORY, value = "零售客户")
    public static final int CUSTOMER_CATEGORY_RETAILER = 1;
    @CodeAnnotation(category = CUSTOMER_CATEGORY, value = "经销商")
    public static final int CUSTOMER_CATEGORY_DEALER = 2;

    public static final String REGION = "地区";
    @CodeAnnotation(category = REGION, value = "国家")
    public static final int REGION_COUNTRY = 0;
    @CodeAnnotation(category = REGION, value = "省")
    public static final int REGION_PROVINCE = 1;
    @CodeAnnotation(category = REGION, value = "城市")
    public static final int REGION_CITY = 2;

    public static final String TRADE_TYPE = "贸易类型";
    @CodeAnnotation(category = TRADE_TYPE, value = "一般贸易")
    public static final int TRADE_TYPE_NORMAL = 1;
    @CodeAnnotation(category = TRADE_TYPE, value = "跨境")
    public static final int TRADE_TYPE_INTERNATIONAL = 2;
    @CodeAnnotation(category = TRADE_TYPE, value = "跨境+一般贸易")
    public static final int TRADE_TYPE_MIX = 3;

    public static final String ACTIVENESS = "活跃度";
    @CodeAnnotation(category = ACTIVENESS, value = "新客户")
    public static final int ACTIVENESS_NEW = 0;
    @CodeAnnotation(category = ACTIVENESS, value = "活跃")
    public static final int ACTIVENESS_ACTIVE = 1;
    @CodeAnnotation(category = ACTIVENESS, value = "不活跃")
    public static final int ACTIVENESS_INACTIVE = 2;
    @CodeAnnotation(category = ACTIVENESS, value = "休眠")
    public static final int ACTIVENESS_SLEEP = 3;

    public static final String VISIT_RESULT = "拜访结果";
    @CodeAnnotation(category = VISIT_RESULT, value = "有成交")
    public static final int VISIT_RESULT_DEAL = 1;
    @CodeAnnotation(category = VISIT_RESULT, value = "有意向")
    public static final int VISIT_RESULT_MIND = 2;
    @CodeAnnotation(category = VISIT_RESULT, value = "无意向")
    public static final int VISIT_RESULT_NO_MIND = 3;
    @CodeAnnotation(category = VISIT_RESULT, value = "其他")
    public static final int VISIT_RESULT_OTHER = 4;

    public static final String AFTER_ACTION = "后续行动";
    @CodeAnnotation(category = AFTER_ACTION, value = "开单")
    public static final int AFTER_ACTION_DEAL = 1;
    @CodeAnnotation(category = AFTER_ACTION, value = "继续拜访")
    public static final int AFTER_ACTION_CONTINUE = 2;
    @CodeAnnotation(category = AFTER_ACTION, value = "不再拜访")
    public static final int AFTER_ACTION_NOT = 3;
    @CodeAnnotation(category = AFTER_ACTION, value = "其他")
    public static final int AFTER_ACTION_OTHER_ = 4;

    public static final String RELEASE_TYPE = "释放类型";
    @CodeAnnotation(category = RELEASE_TYPE, value = "未拜访")
    public static final int RELEASE_TYPE_VISIT = 1;
    @CodeAnnotation(category = RELEASE_TYPE, value = "未成交")
    public static final int RELEASE_TYPE_DEAL = 2;
    @CodeAnnotation(category = RELEASE_TYPE, value = "未复购")
    public static final int RELEASE_TYPE_REPEAT = 3;

    public static final String AUTH_TYPE = "授权类型";
    @CodeAnnotation(category = AUTH_TYPE, value = "销售授权")
    public static final int AUTH_TYPE_SALE = 1;
    @CodeAnnotation(category = AUTH_TYPE, value = "商标授权")
    public static final int AUTH_TYPE_TRADEMARK = 2;
    @CodeAnnotation(category = AUTH_TYPE, value = "合并授权")
    public static final int AUTH_TYPE_COMBINE = 3;

    public static final String KPI_STATUS = "绩效考核状态";
    @CodeAnnotation(category = KPI_STATUS,value = "绩效待制定")
    public static final int KPI_STATUS_1 = 1;
    @CodeAnnotation(category = KPI_STATUS,value = "已反馈(待制定)")
    public static final int KPI_STATUS_11 = 11;
    @CodeAnnotation(category = KPI_STATUS,value = "同意改进(待制定)")
    public static final int KPI_STATUS_12 = 12;
    @CodeAnnotation(category = KPI_STATUS,value = "绩效已制定")
    public static final int KPI_STATUS_2 = 2;
    @CodeAnnotation(category = KPI_STATUS,value = "已确认(待提交HR)")
    public static final int KPI_STATUS_3 = 3;
    @CodeAnnotation(category = KPI_STATUS,value = "已确认(待考核)")
    public static final int KPI_STATUS_4 = 4;
    @CodeAnnotation(category = KPI_STATUS,value = "自评完毕")
    public static final int KPI_STATUS_5 = 5;
    @CodeAnnotation(category = KPI_STATUS,value = "上级评价完毕")
    public static final int KPI_STATUS_6 = 6;
    @CodeAnnotation(category = KPI_STATUS,value = "成员提出改进")
    public static final int KPI_STATUS_7 = 7;
    @CodeAnnotation(category = KPI_STATUS,value = "已确认(考核结束)")
    public static final int KPI_STATUS_8 = 8;
    @CodeAnnotation(category = KPI_STATUS,value = "已驳回(考核结束)")
    public static final int KPI_STATUS_9 = 9;
    @CodeAnnotation(category = KPI_STATUS,value = "离职考核完毕")
    public static final int KPI_STATUS_30 = 30;

}
