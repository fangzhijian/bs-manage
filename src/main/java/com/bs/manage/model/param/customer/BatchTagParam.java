package com.bs.manage.model.param.customer;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 2020/2/19 13:37
 * fzj
 */
@Data
public class BatchTagParam implements Serializable {

    private static final long serialVersionUID = 5794600965813074587L;

    private List<Long> ids;         //客户id
    private List<Long> tags;        //标签id
}
