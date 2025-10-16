package com.bs.manage.model.param.customer;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 2020/4/7 9:57
 * fzj
 */
@Data
public class CustomerReleaseSearchParam implements Serializable {

    private static final long serialVersionUID = -4644647478868386404L;

    @NotNull
    private Integer limit;                               //每页条数
    @NotNull
    private Integer offset;                              //第几条开始
    private Long process_user_id;                        //跟进人
    private Integer allocate_again;                      //是否查询重复分配 0-否 1-是


    private List<Long> process_user_ids;                 //下级所有跟进人id

}
