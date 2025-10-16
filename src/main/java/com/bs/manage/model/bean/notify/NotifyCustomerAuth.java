package com.bs.manage.model.bean.notify;

import com.bs.manage.model.bean.common.CommonModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

/**
 * 2020/4/13 11:54
 * fzj
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("notifyCustomerAuth")
@SuperBuilder
@NoArgsConstructor
public class NotifyCustomerAuth extends CommonModel {

    private static final long serialVersionUID = 6062722595110689447L;

    private Long customer_shop_id;        //客户店铺id
    private String customer_name;         //客户名称
    private Integer auth_type;            //授权类型
    private String platform;              //授权平台
    private LocalDateTime expire_time;    //过期时间
    private Long brand_id;                //品牌id
    private String brand_name;            //品牌名称
    private String link;                  //证书链接
    private Long notify_user;             //通知人
    private Integer has_read;             //是否已读 0-未读 1-已读
}
