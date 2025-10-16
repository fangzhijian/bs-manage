package com.bs.manage.model.param.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 2020/2/19 9:28
 * fzj
 */
@Data
public class UserInsertParam implements Serializable {

    private static final long serialVersionUID = -5809372383488566929L;

    @NotBlank
    private String name;                //姓名
//    @Email 取消邮箱
    @NotBlank
    private String email;               //邮箱(登录名)
    private String password;            //登录密码
    @NotBlank
    private String phone;               //钉钉关联手机号
    private Integer role;               //角色 1-专员 2-管理员 3-推广
    @Email
    private String position;            //岗位名称;
    private List<Integer> resources;    //指定权限
    private Integer customer_limit;     //客户上限

}
