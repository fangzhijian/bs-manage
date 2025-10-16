package com.bs.manage.model.bean.account;

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
@Alias("userTags")
@SuperBuilder
@NoArgsConstructor
public class UserTags extends CommonModel {

    private static final long serialVersionUID = 6492472314051655570L;

    private Long user_id; //账号id
    private String name;  //标签名称
}
