package com.bs.manage.model.bean.common;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 2020/1/17 11:42
 * fzj
 */
@Data
@SuperBuilder
@NoArgsConstructor
public class CommonModel implements Serializable {

    private static final long serialVersionUID = 5674065068327307258L;

    private Long id;                    //主键id
    private LocalDateTime created_at;   //创建时间
    private LocalDateTime updated_at;   //更新时间
}
