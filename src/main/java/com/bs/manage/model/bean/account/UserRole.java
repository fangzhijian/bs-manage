package com.bs.manage.model.bean.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.util.List;

/**
 * 2020/2/25 9:48
 * fzj
 */
@Data
@Alias("userRole")
public class UserRole implements Serializable {

    private static final long serialVersionUID = 7094558398853715751L;

    public static List<UserRole> userRoles;

    private Integer id;         //id
    private String label;       //标签说明`
    @JsonIgnore
    private String api;         //uri
    @JsonIgnore
    private String method;      //请求类型
    @JsonIgnore
    private Integer parent_id;  //上级id
}
