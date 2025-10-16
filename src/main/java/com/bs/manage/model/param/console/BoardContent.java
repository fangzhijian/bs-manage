package com.bs.manage.model.param.console;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 2020/3/16 11:19
 * fzj
 */
@Data
@Accessors(chain = true)
public class BoardContent implements Serializable {

    private static final long serialVersionUID = -6626059225015271199L;

    private String label;                //标签
    private String value;                //值
    private String percent;              //占比
    private List<BoardContent> child;    //下级数值(客户会有S T Y C下级)

}
