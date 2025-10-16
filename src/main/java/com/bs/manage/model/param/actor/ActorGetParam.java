package com.bs.manage.model.param.actor;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 2020/2/19 12:03
 * fzj
 */
@Data
public class ActorGetParam implements Serializable {

    private static final long serialVersionUID = 3730739773156832670L;

    @NotNull
    private Integer limit;                      //分页一页条数
    @NotNull
    private Integer offset;                     //分页偏移量
    private String keyword;                     //查询关键字
    private List<Long> actor_category_id2;      //属性二级id
    private List<Long> level;                   //级别
    private List<Long> city_id;                 //城市id
    private List<Long> class_id;                //类目id
    private String created_at;                  //录入时间 yyyy-MM-dd
    private Long process_user_id;               //跟进人
}
