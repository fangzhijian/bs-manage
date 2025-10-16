package com.bs.manage.model.bean.customer;

import com.bs.manage.model.bean.common.CommonModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.Alias;

/**
 * 2020/5/29 15:56
 * fzj
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("customerBindLog")
@SuperBuilder
@NoArgsConstructor
public class CustomerBindLog extends CommonModel {

    private static final long serialVersionUID = -382112040267784484L;

    private Long customer_id;           //客户id
    private Long user_id;               //账号id
    private Integer action;             //行为 1-自己捡起 2-上级分配
}
