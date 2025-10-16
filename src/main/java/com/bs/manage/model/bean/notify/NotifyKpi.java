package com.bs.manage.model.bean.notify;

import com.bs.manage.model.bean.common.CommonModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.Alias;

/**
 * 2020/6/16 19:18
 * fzj
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("notifyKpi")
@SuperBuilder
@NoArgsConstructor
public class NotifyKpi extends CommonModel {

    private static final long serialVersionUID = -1907553944071497700L;

    private Long kpi_id;                //kpi主表id
    private String text;                //提醒内容
    private Long notify_user;           //提醒人
    private Integer has_read;           //是否已读 0-未读 1-已读
}
