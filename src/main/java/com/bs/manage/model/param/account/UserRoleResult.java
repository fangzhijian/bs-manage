package com.bs.manage.model.param.account;

import com.bs.manage.model.bean.account.UserRole;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 2020/2/25 10:08
 * fzj
 */
@Data
public class UserRoleResult implements Serializable {

    private static final long serialVersionUID = -4318780869923837622L;

    private Integer parent_id;                              //上级id
    private String label;                                   //标签
    private List<UserRole> child_role = new ArrayList<>();  //子权限集合
}
