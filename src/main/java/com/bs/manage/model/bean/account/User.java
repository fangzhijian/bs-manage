package com.bs.manage.model.bean.account;

import com.bs.manage.model.bean.common.CommonModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.Alias;

import java.util.List;

/**
 * 2020/1/15 10:49
 * fzj
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("users")
@SuperBuilder
@NoArgsConstructor
public class User extends CommonModel {

    private static final long serialVersionUID = 1948164900590172896L;

    private String name;                //姓名
    private String email;               //邮箱(登录名)
    private String password;            //密码
    private String api_token;           //token
    private String phone;               //手机号
    private String dd_user_id;          //所在公司的钉钉userId
    private Integer role;               //角色 1-专员 2-管理员 3-推广
    private String role_caption;        //角色说明
    private String resources;           //指定权限
    private Integer status;             //账号状态 1正常，2停用 ,3-离职
    private String status_caption;      //账号状态说明
    private Long team_id;               //团队id
    private String team_name;           //团队名称
    private Long parent_id;             //上级id
    private String parent_name;         //上级姓名
    private Integer has_child;          //是否有下级 0-没有 1-有
    private String position;            //岗位名称;
    private Integer customer_limit;     //客户上限

    private List<Integer> roleIds;      //指定权限集合

    private UserInfo  user_info;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor(staticName = "of")
    public static class UserInfo{
        private String nick_name;
    }

}
