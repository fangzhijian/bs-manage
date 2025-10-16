package com.bs.manage.model.param.console;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 2020/3/19 19:02
 * fzj
 */
@Data
@Accessors(chain = true)
@SuperBuilder
@NoArgsConstructor
public class BoardTable implements Serializable {

    private static final long serialVersionUID = 4082711840026436373L;

    private String label;            //标签
    private BigDecimal column_1;
    private BigDecimal column_2;
    private BigDecimal column_3;
    private BigDecimal column_4;
    private BigDecimal column_5;
    private BigDecimal column_6;
    private BigDecimal column_7;
    private BigDecimal column_8;
    private BigDecimal column_9;
    private List<BoardTable> child;  //下级值,字段同上


}
