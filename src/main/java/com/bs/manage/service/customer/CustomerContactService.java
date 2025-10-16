package com.bs.manage.service.customer;

import com.bs.manage.model.bean.customer.CustomerContact;
import com.bs.manage.service.common.CommonService;

import java.util.List;

/**
 * 2020/3/13 17:04
 * fzj
 */
public interface CustomerContactService extends CommonService<CustomerContact> {

    /**
     * 根据客户批量插入联系人
     *
     * @param list        联系人信息
     * @param customer_id 客户id
     */
    void insertBatchAsCustomer(List<CustomerContact> list,Long customer_id);
}
