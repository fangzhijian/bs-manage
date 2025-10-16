package com.bs.manage.model.bean.notify;

import com.bs.manage.model.bean.common.CommonModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.Alias;

/**
 * 2020/4/13 11:47
 * fzj
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("notifyCustomerRelease")
@SuperBuilder
@NoArgsConstructor
public class NotifyCustomerRelease extends CommonModel {

    private static final long serialVersionUID = -4650730086751793629L;

    private String sn;                           //客户编号
    private Long customer_id;                    //客户id
    private String customer_name;                //客户名称
    private Long process_user_id;                //跟进人id
    private String process_user_name;            //跟进人姓名
    private Long customer_category_id;           //客户属性 一级
    private String customer_gradation;           //客户分层 S T Y C
    private Integer release_type;                //释放的类型
    private Integer left_day;                    //剩余天数
    private Long notify_user;                    //提醒人
    private Integer has_read;                    //是否已读 0-未读 1-已读
}
