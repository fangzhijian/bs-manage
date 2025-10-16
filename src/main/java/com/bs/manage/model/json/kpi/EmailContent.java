package com.bs.manage.model.json.kpi;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 2020/6/15 16:55
 * fzj
 */
@Data
public class EmailContent implements Serializable {

    private static final long serialVersionUID = 849408002163008002L;

    private String title; //标题
    @NotBlank
    private String text;  //内容
}
