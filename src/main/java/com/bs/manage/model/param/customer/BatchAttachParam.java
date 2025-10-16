package com.bs.manage.model.param.customer;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 2020/2/19 13:40
 * fzj
 */
@Data
public class BatchAttachParam implements Serializable {

    private static final long serialVersionUID = 4762004228997978264L;

    private List<Long> ids;     //客户id
    private Long user_id;       //账号id
}
