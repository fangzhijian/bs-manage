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
 * 2023/4/18 15:58
 * fzj
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("returnWarehouseDetail")
@SuperBuilder
@NoArgsConstructor
public class ReturnWarehouseDetail extends CommonModel {

    private static final long serialVersionUID = 5093396572164154425L;

    private String warehouse_entry_no;             //入库单号
    private String warehouse_entry_state;          //入库状态
    private String shop_no;                        //商家编码
    private String goods_no;                       //货品编码
    private String goods_name;                     //货品名称
    private String specification_name;             //规格名称
    private String specification;                  //规格码
    private String brand;                          //品牌
    private String category;                       //分类
    private Integer sales_return_num;              //退货量
    private String warehouse;                      //入库仓库
    private Integer warehouse_entry_num;           //入库量
    private Integer adjust_num;                    //调整后数量
    private BigDecimal unit_price;                 //单价
    private BigDecimal entry_total_amount;         //入库总额
    private BigDecimal adjust_total_amount;        //调整后总额
    private String entry_batch;                    //入库批次
    private String entry_goods_allocation;         //入库货位
    private String return_no;                      //退换单号
    private String return_state;                   //退换单状态
    private String order_num;                      //来源订单号
    private String customer_net_name;              //客户网名
    private String consignee;                      //收件人姓名
    private String shop_name;                      //店铺
    private String return_reason;                  //退换原因
    private LocalDateTime make_time;               //建单时间
    private String original_order_num;             //原始单号
    private String distributor;                    //分销商
    private Integer source_type;                   //1-平台 2-直播
    private String order_type;                     //订单类型
}
