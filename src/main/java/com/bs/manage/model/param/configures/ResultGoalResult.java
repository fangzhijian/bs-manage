package com.bs.manage.model.param.configures;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 2020/4/28 14:28
 * fzj
 */
@Data
public class ResultGoalResult implements Serializable {

    private static final long serialVersionUID = -8655208371289756719L;

    private BigDecimal team_total;             //团队总目标金额
    private List<ResultGoalDisplay> displays;  //具体展示列表
}
