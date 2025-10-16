package com.bs.manage.service.customer;

import com.bs.manage.model.bean.customer.CustomerShop;
import com.bs.manage.service.common.CommonService;

import java.util.List;

/**
 * 2020/7/24 10:30
 * fzj
 */
public interface CustomerShopService extends CommonService<CustomerShop> {

    CustomerShop getShopDetail(Long customer_shop_id);

    void deleteByParentUniqueId(String parentUniqueId);

    List<CustomerShop> getAllShop(String parentUniqueId);

    /**
     * 查询客户店铺是否已存在重复
     *
     * @param keyword 客户名称或唯一ID
     * @return 客户列表
     */
    List<CustomerShop> judgeRepeat(String keyword);

}
