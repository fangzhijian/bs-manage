package com.bs.manage.model.bean.console;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;


/**
 * 2020/3/18 20:16
 * fzj
 */
@Data
@Alias("idAndCount")
@Accessors(chain = true)
public class IdAndCount implements Serializable {

    private static final long serialVersionUID = 5767216287754258349L;

    private Long user_id;               //账号id
    private Integer total;              //总数量
    private Integer anther_total;       //另一个总数量
    private Long customer_category_id;  //客户一级属性id
    private String customer_gradation;  //客户分层
    private Long team_id;               //团队id

}
