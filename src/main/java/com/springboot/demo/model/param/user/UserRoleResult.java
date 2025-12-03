package com.springboot.demo.model.param.user;

import com.springboot.demo.model.bean.account.UserRole;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 2020/2/25 10:08
 * fzj
 */
@Data
public class UserRoleResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer parent_id;                              //上级id
    private String label;                                   //标签
    private List<UserRole> child_role = new ArrayList<>();  //子权限集合
}
