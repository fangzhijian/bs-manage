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
 * 2023/4/17 16:43
 * fzj
 */

@EqualsAndHashCode(callSuper = true)
@Data
@Alias("salesReleaseDetail")
@SuperBuilder
@NoArgsConstructor
public class SalesReleaseDetail extends CommonModel {

    private static final long serialVersionUID = -2565223082747565546L;

    private String order_num;                         //订单编号
    private String original_order_num;                //原始单号
    private String sun_original_no;                   //子原始单号
    private String original_sun_order_no;             //原始子订单号
    private String order_type;                        //订单类型
    private String pay_account;                       //支付账号
    private String stock_out_num;                     //出库单编号
    private String warehouse;                         //仓库
    private String warehouse_type;                    //仓库类型
    private String shop_name;                         //店铺
    private String delivery_no_state;                 //出库单状态
    private String delivery_state;                    //出库状态
    private String sorting_number;                    //分拣序号
    private String shop_no;                           //商家编码
    private String goods_no;                          //货品编码
    private String goods_name;                        //货品名称
    private String goods_short_name;                  //货品简称
    private String brand;                             //品牌
    private String category;                          //分类
    private String specification;                     //规格码
    private String specification_name;                //规格名称
    private String bar_code;                          //条形码
    private Integer goods_nums;                       //货品数量
    private BigDecimal good_origin_unit_price;        //货品原单价
    private BigDecimal good_origin_total_pay;         //货品原总金额
    private BigDecimal order_discounts;               //订单总优惠
    private BigDecimal postage;                       //邮费
    private BigDecimal goods_amount;                  //货品成交价
    private BigDecimal goods_total_amount;            //货品成交总价
    private BigDecimal goods_discounts;               //货品总优惠
    private BigDecimal goods_total_pay;               //货到付款总金额
    private BigDecimal goods_cost;                    //货品成本
    private BigDecimal goods_total_cost;              //货品总成本
    private BigDecimal order_pay_amount;              //订单支付金额
    private BigDecimal amount_receivable;             //应收金额
    private BigDecimal before_refund_amount;          //退款前支付金额
    private BigDecimal single_pay_amount;             //单品支付金额
    private BigDecimal apportion_postage;             //分摊邮费
    private BigDecimal estimate_postage;              //预估邮资
    private BigDecimal postage_cost;                  //邮资成本
    private BigDecimal order_pack_cost;               //订单包装成本
    private BigDecimal order_gross_profit;            //订单毛利
    private BigDecimal gross_profit_rate;             //毛利率
    private String customer_net_name;                 //客户网名
    private String consignee;                         //收件人
    private String id_card;                           //证件号码
    private String receiving_area;                    //收货地区
    private String receiver_address;                  //收货地址
    private String receiver_mobile_phone;             //收件人手机
    private String receiver_phone;                    //收件人电话
    private String logistics_company;                 //物流公司
    private BigDecimal actual_weight;                 //实际重量
    private BigDecimal estimate_weight;               //预估重量
    private String need_invoice;                      //需开发票
    private String make_order_user;                   //制单人
    private String print_user;                        //打单员
    private String order_picking_user;                //拣货员
    private String pack_user;                         //打包员
    private String inspector_user;                    //检视员
    private String salesman;                          //业务员
    private String examine_goods_user;                //验货员
    private String print_num;                         //打印波次
    private String logistics_print_status;            //物流单打印状态
    private String deliver_print_status;              //发货单打印状态
    private String sorting_print_status;              //分拣单打印状态
    private String logistics_num;                     //物流单号
    private String sorting_num;                       //分拣单编号
    private String external_order_num;                //外部单号
    private LocalDateTime pay_time;                   //付款时间
    private LocalDateTime deliver_time;               //发货时间
    private String gift_way;                          //赠品方式
    private String buyer_message;                     //买家留言
    private String customer_service_remark;           //客服备注
    private String print_remark;                      //打印备注
    private String remark;                            //备注
    private String packaging;                         //包装
    private String combination_package_no;            //来源组合装编码
    private String split_combination_package;         //拆自组合装
    private Integer combination_package_num;          //来源组合装数量
    private BigDecimal volume;                        //体积
    private String distributor;                       //分销商
    private LocalDateTime order_time;                 //下单时间
    private LocalDateTime audit_time;                 //审核时间
    private String distributor_no;                    //分销商编号
    private Integer source_type;                      //1-平台 2-直播
}
