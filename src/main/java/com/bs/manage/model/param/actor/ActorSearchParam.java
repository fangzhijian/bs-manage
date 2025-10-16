package com.bs.manage.model.param.actor;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 2020/3/5 15:07
 * fzj
 */
@Data
public class ActorSearchParam implements Serializable {

    private static final long serialVersionUID = 8745663227232339787L;

    @NotNull
    private Integer limit;              //每页条数
    @NotNull
    private Integer offset;             //第几条开始
    private String keyword;             //关键字搜索达人编号或达人名称或平台id
    private String ids;                 //勾选的多个id,逗号隔开

    private String wx_id;                       //微信号
    private String label;                       //标签
    private String homepage_link;               //主页链接
    private String organization;                //所属机构
    private String attribute_one;               //一级属性
    private String attribute_two;               //二级属性
    private String attribute_extension;         //推广属性
    private BigDecimal quoted_price;            //合作报价
    private Integer fans_num;                   //粉丝数
    private String province;                    //省
    private String city;                        //市
    private String shipping_address;            //收货地址
    private String linkman;                     //联系人
    private String contact_number;              //联系电话
    private String job_wx;                      //对接微信
    private String business_owner;              //业务负责人
    private String total_view;                  //总播放量
    private String average_view;                //平均观场
    private String remark;                      //备注
    private String create_start;                //创建时间开始,yyyy-MM-dd HH:mm:ss
    private String create_end;                  //创建时间结束,yyyy-MM-dd HH:mm:ss

    private Long create_user_id;        //录入人
}
