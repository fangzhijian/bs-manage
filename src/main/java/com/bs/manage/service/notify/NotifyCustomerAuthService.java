package com.bs.manage.service.notify;

import com.bs.manage.model.bean.notify.NotifyCustomerAuth;
import com.bs.manage.model.json.Page;
import com.bs.manage.service.common.CommonService;

/**
 * 2020/4/13 11:56
 * fzj
 */
public interface NotifyCustomerAuthService extends CommonService<NotifyCustomerAuth> {

    /**
     * 查询未读消息数
     *
     * @return 未读消息数
     */
    Integer countByUnRead();

    /**
     * 分页查询客户授权过期通知
     *
     * @param limit  每页条数
     * @param offset 第几条开始
     * @return 客户授权过期通知列表
     */
    Page<NotifyCustomerAuth> getByPage(Integer limit, Integer offset);

}
