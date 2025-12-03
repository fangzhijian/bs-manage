package com.springboot.demo.model.bean.account;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 2020/2/25 9:48
 * fzj
 */
@Data
@TableName(value = "user_role")
public class UserRole implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.INPUT)
    private Integer id;         //id
    private String label;       //标签说明`
    @JsonIgnore
    private String api;         //uri
    @JsonIgnore
    private String method;      //请求类型
    @JsonIgnore
    private Integer parent_id;  //上级id

    public static List<UserRole> userRoles;
}
