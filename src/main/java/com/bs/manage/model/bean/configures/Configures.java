package com.bs.manage.model.bean.configures;

import com.bs.manage.model.bean.common.CommonModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.Alias;

import jakarta.validation.constraints.NotBlank;

/**
 * 2020/3/12 9:57
 * fzj
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("configures")
@SuperBuilder
@NoArgsConstructor
public class Configures extends CommonModel {

    private static final long serialVersionUID = 5134962465766505670L;

    private Integer code;              //code数字
    @NotBlank
    private String code_value;         //code对应的值
    @NotBlank
    private String note;               //备注说明
}
