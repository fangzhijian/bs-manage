package com.bs.manage.service.customer;

import com.bs.manage.model.bean.customer.CustomerBindLog;
import com.bs.manage.service.common.CommonService;

import java.util.List;

/**
 * 2020/5/29 16:02
 * fzj
 */
public interface CustomerBindLogService extends CommonService<CustomerBindLog> {

    /**
     * 查询最近的绑定者
     *
     * @param list 客户id列表
     * @return 最新的绑定列表
     */
    List<CustomerBindLog> getNearBindForCustomerIds(List<Long> list);
}
