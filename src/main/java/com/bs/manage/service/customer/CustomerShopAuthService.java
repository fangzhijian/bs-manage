package com.bs.manage.service.customer;

import com.bs.manage.model.bean.customer.CustomerShopAuth;
import com.bs.manage.service.common.CommonService;

/**
 * 2020/4/13 9:55
 * fzj
 */
public interface CustomerShopAuthService extends CommonService<CustomerShopAuth> {


    /**
     * 新增客户授权到期提醒
     */
    void insertToNotifyCustomerAuth();

}
