package com.bs.manage.model.bean.customer;

import com.bs.manage.model.bean.common.CommonModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.Alias;

/**
 * 2020/3/2 16:53
 * fzj
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("customerTagRelation")
@SuperBuilder
@NoArgsConstructor
public class CustomerTagRelation extends CommonModel {

    private static final long serialVersionUID = 300712155199522708L;

    private Long customer_id;       //客户id
    private Long user_tag_id;       //账号拥有的标签id
    private String user_tag_name;   //账号拥有的标签名称
}
