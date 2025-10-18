package com.bs.manage.model.bean.account;

import com.bs.manage.model.bean.common.CommonDeleteModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.Alias;

/**
 * 2020/1/20 15:14
 * fzj
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper=true)
@Alias("team")
@SuperBuilder
@NoArgsConstructor
public class Team extends CommonDeleteModel {

    private static final long serialVersionUID = -383379726895381319L;

    private String name;         //团队名称

    private Integer users_count; //团队成员数

    private Long leader_id;      //leader的账号id

    private String leader_name;  //leader的姓名

}
