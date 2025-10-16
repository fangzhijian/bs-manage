package com.bs.manage.model.bean.customer;

import com.bs.manage.model.bean.common.CommonModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.Alias;

import java.util.List;

/**
 * 2020/4/7 9:36
 * fzj
 */
@EqualsAndHashCode(callSuper = true)
@Alias("customerRelease")
@Data
@SuperBuilder
@NoArgsConstructor
public class CustomerRelease extends CommonModel {

    private static final long serialVersionUID = 4103704529245780178L;

    private String sn;                         //客户编号
    private String name;                       //客户名称
    private Long process_user_id;              //跟进人
    private String process_user_name;          //跟进人姓名
    @JsonIgnore
    private Long team_id;                      //跟进人的团队id
    private Long customer_category_id;         //客户属性 一级
    private String customer_gradation;         //客户分层 S T Y C
    private Integer release_type;              //释放的类型
    private String release_type_label;         //释放的类型说明
    private Integer left_day;                  //剩余天数
    private Integer allocate_again;            //重复分配次数

    private List<Long> process_user_ids;       //下级所有跟进人id
}
