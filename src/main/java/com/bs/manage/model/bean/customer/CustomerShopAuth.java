package com.bs.manage.model.bean.customer;

import com.bs.manage.model.bean.common.CommonModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.Alias;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;

/**
 * 2020/4/13 9:51
 * fzj
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("customerShopAuth")
@SuperBuilder
@NoArgsConstructor
public class CustomerShopAuth extends CommonModel {

    private static final long serialVersionUID = -1817754441569646623L;

    @NotNull
    private Long customer_shop_id;           //客户店铺id
    @NotNull
    @Range(min = 1, max = 3)
    private Integer auth_type;          //授权类型 1-销售授权、2-商标授权、3-合并授权
    private String auth_type_label;     //授权类型说明
    @NotBlank
    private String platform;            //授权平台
    @NotNull
    private LocalDateTime expire_time;  //过期时间,格式yyyy-MM-dd HH:mm:ss
    @NotNull
    private Long brand_id;              //品牌id
    @NotBlank
    private String brand_name;          //品牌名称
    @NotNull
    private Long data_id;               //证书id
    private String link;                //证书链接
    private String data_name;           //证书文件名
}
