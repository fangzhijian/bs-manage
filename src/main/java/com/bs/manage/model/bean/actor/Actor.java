package com.bs.manage.model.bean.actor;

import com.bs.manage.model.bean.common.CommonDeleteModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.Alias;

import java.math.BigDecimal;

/**
 * 2020/3/5 15:12
 * fzj
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("actor")
@SuperBuilder
@NoArgsConstructor
public class Actor extends CommonDeleteModel {

    private static final long serialVersionUID = 6278389100134969317L;

    private String sn;                          //达人编号
    private String actor_name;                  //达人名称
    private String platform_id;                 //平台id
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
    private Long create_user_id;                //录入人id
    private String create_user_name;            //录入人姓名
    private String remark;                      //备注


    private String insertError;                     //插入失败信息

}
