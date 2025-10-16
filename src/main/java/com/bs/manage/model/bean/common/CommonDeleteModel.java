package com.bs.manage.model.bean.common;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 2020/1/17 11:44
 * fzj
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
public class CommonDeleteModel extends CommonModel {

    private static final long serialVersionUID = 3065339848143610843L;

    private LocalDateTime deleted_at;       //删除时间
    private Integer delete_status;          //删除状态 0-未删除 1-已删除
}
