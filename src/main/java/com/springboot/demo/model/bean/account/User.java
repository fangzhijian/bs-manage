package com.springboot.demo.model.bean.account;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 2020/1/15 10:49
 * fzj
 */
@Data
@TableName(value = "users")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 姓名
     */
    private String name;
    /**
     * 密码
     */
    private String password;
    /**
     * token
     */
    private String apiToken;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 指定权限
     */
    private Integer role;
    /**
     * 指定权限
     */
    private String resources;
    /**
     * 账号状态 1正常，2停用 ,3-离职
     */
    private Integer status;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 权限合集
     */
    @TableField(exist = false)
    private List<Integer> roleIds;      //指定权限集合


}
