package com.bs.manage.model.bean.erp;

import com.bs.manage.model.bean.common.CommonModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.Alias;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 2023/4/10 17:09
 * fzj
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("returnWarehouseEntry")
@SuperBuilder
@NoArgsConstructor
public class ReturnWarehouseEntry extends CommonModel {

    private static final long serialVersionUID = 3474802532193954150L;

    private String warehouse_entry_no;              //入库单号
    private String warehouse_entry_state;           //入库单状态
    private String type;                            //类型
    private String return_state;                    //退换单状态
    private String return_reason;                   //退换原因
    private String return_remark;                   //退换备注
    private String return_creator;                  //退换创建人
    private String shop_name;                       //店铺
    private String original_order_num;              //原始订单
    private String order_num;                       //订单编号
    private String order_logistics_company;         //订单物流公司
    private String order_logistics_num;             //订单物流单号
    private String customer_net_name;               //客户网名
    private String return_no;                       //退换单号
    private String warehouse_entry_user;            //入库人
    private String actual_warehouse;                //实际入库仓库
    private String logistics_company;               //物流公司
    private String logistics_num;                   //物流单号
    private BigDecimal return_total_income;         //退回货款收入
    private BigDecimal return_cost;                 //退回货品成本
    private BigDecimal discounts;                   //优惠
    private BigDecimal postage;                     //邮资
    private BigDecimal other_balance;               //其他余额
    private BigDecimal adjust_amount;               //调整后总金额
    private Integer expect_num;                     //预期数量
    private Integer goods_num;                      //货品数量
    private Integer goods_type_num;                 //货品类型数量
    private Integer adjust_num;                     //调整后数量
    private String remark;                          //备注
    private LocalDateTime make_time;                //制单时间
    private LocalDateTime update_time;              //修改时间
    private LocalDateTime audit_time;               //审核时间
    private String audit_user;                      //审核人
    private String push_state;                      //推送状态
    private String push_error_msg;                  //推送失败原因
    private String distributor;                     //分销商名称
    private Integer source_type;                   //1-平台 2-直播
    private String order_type;                     //订单类型
}
