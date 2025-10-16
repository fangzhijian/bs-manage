package com.bs.manage.model.json;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 2020/2/25 16:29
 * fzj
 */
@Data
public class Page<T> implements Serializable {

    private static final long serialVersionUID = -3652988480964967780L;

    private Integer total;                      //总条数
    private List<T> items = new ArrayList<>();  //内容列表
}
