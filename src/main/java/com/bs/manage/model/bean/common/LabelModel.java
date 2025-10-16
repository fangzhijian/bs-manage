package com.bs.manage.model.bean.common;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 2020/2/26 13:40
 * fzj
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
public class LabelModel extends CommonDeleteModel{

    private static final long serialVersionUID = -8762935932020259670L;

    private String name;        //名称
    private Long parent_id;     //上级id

}
