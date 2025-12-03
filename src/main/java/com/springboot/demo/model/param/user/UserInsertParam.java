package com.springboot.demo.model.param.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 2020/2/19 9:28
 * fzj
 */
@Data
public class UserInsertParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank
    private String name;                //姓名
    private String password;            //登录密码
    @NotBlank
    private String phone;               //钉钉关联手机号
    private Integer role;               //角色 1-专员 2-管理员 3-推广
    private List<Integer> resources;    //指定权限

}
