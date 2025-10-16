package com.bs.manage.model.param.configures;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 2020/4/26 16:57
 * fzj
 */
@Data
public class ProcessGoalDisplay implements Serializable {

    private static final long serialVersionUID = 4605823117305974483L;

    private Long user_id;               //账号id
    private String user_name;           //账号名称
    private List<ProcessGoalInfo> infos;//详细信息
}
