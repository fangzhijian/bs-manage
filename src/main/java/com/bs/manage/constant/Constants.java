package com.bs.manage.constant;

/**
 * 2020/3/18 14:15
 * fzj
 */
public interface Constants {

    /**
     * 参数配置
     */
    int CONFIG_RELEASE_REPEAT = 100;    //客户未复购释放时间,单位天
    int CONFIG_RELEASE_DEAL = 101;      //客户未成交释放时间,单位天
    int CONFIG_RELEASE_VISIT = 102;     //客户未拜访释放时间,单位天
    int CONFIG_CUSTOMER_LEVEL = 200;    //客户淘宝等级是否允许修改和删除,0不能、1能
    int CONFIG_REPORT_INSERT = 300;     //日报是否可以选择日期,1-可以 0-不可以
    int CONFIG_REPORT1_UPDATE = 301;    //是否可以删除或修改任意时间的日报 1-可以 0-不可以
    int CONFIG_NOTIFY_USER = 400;       //客户无跟进人但授权快过期时,指定通知的账号id
    int CONFIG_KPI_HR = 500;            //考核通知的HR账号id
    int CONFIG_KPI_DATE = 501;          //考核制定的最后时间,不大于20号


    String DEFAULT_DING_TITLE = "云系统消息通知";                   //钉钉工作通知默认标题
    String DEFAULT_DING_PIC_URL = "http://data.hzleadspeed.com/upload/auth/20200508/15889029733165059.jpg";//钉钉工作通知默认图片url
}
