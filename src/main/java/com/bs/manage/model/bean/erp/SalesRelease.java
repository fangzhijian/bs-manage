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
 * 2023/4/6 13:33
 * fzj
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("salesRelease")
@SuperBuilder
@NoArgsConstructor
public class SalesRelease extends CommonModel {

    private static final long serialVersionUID = -6355823009018748181L;

    private String order_num;                         //订单编号
    private String stock_out_num;                     //出库单编号
    private String warehouse;                         //仓库
    private String warehouse_type;                    //仓库类型
    private String shop_name;                         //店铺
    private String order_type;                        //订单类型
    private LocalDateTime order_time;                 //下单时间
    private LocalDateTime pay_time;                   //支付时间
    private String deliver_count_down;                //发货倒计时
    private String status;                            //状态
    private String original_order_num;                //原始单号
    private String deliver_goods_status;              //发货状态
    private String deliver_goods_condition;           //发货条件
    private String push_information;                  //推送信息
    private String freezing_cause;                    //冻结原因
    private String sorting_number;                    //分拣序号
    private String make_order_user;                   //制单人
    private Integer goods_nums;                       //货品数量
    private Integer goods_category;                   //货品种类
    private String customer_net_name;                 //客户网名
    private String consignee;                         //收货人
    private String receiving_area;                    //收货地区
    private String receiver_address;                  //收货地址
    private String receiver_mobile_phone;             //收件人手机
    private String receiver_phone;                    //收件人电话
    private String email;                             //邮箱
    private String logistics_company;                 //物流公司
    private BigDecimal total_cost;                    //总成本
    private BigDecimal estimate_postage;              //预估邮资
    private BigDecimal postage_cost;                  //邮资成本
    private BigDecimal estimate_weight;               //预估重量
    private BigDecimal actual_weight;                 //实际重量
    private BigDecimal amount_receivable;             //应收金额
    private BigDecimal amount_paid;                   //已付金额
    private BigDecimal amount_COD;                    //COD金额
    private String contain_invoice;                   //是否包含发票
    private String salesman;                          //业务员
    private String audit_user;                        //审单员
    private String finance_audit_user;                //财审员
    private String print_user;                        //打单员
    private String order_picking_user;                //拣货员
    private String pack_user;                         //打包员
    private String examine_goods_user;                //验货员
    private String inspector_user;                    //检视员
    private String deliver_goods_user;                //发货员
    private String print_num;                         //打印波次
    private String logistics_print_status;            //物流单打印状态
    private String deliver_print_status;              //发货单打印状态
    private String sorting_print_status;              //分拣单打印状态
    private String logistics_num;                     //物流单号
    private String sorting_num;                       //分拣单编号
    private LocalDateTime deliver_time;               //发货时间
    private String external_order_num;                //外部单号
    private String check_out_user;                    //签出人
    private String packaging;                         //包装
    private Integer dispose_day;                      //处理天数
    private LocalDateTime audit_create_time;          //审核创单时间
    private String Interception_cause;                //拦截原因
    private String print_remark;                      //打印备注
    private BigDecimal volume;                        //体积
    private String distributor;                       //分销商
    private String distribution_origin_order;         //分销原始单号
    private String goods_shop_num;                    //货品商家编码
    private Integer source_type;                      //1-平台 2-直播
}
