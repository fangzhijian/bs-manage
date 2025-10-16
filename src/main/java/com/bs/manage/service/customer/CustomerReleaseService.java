package com.bs.manage.service.customer;

import com.bs.manage.model.bean.customer.CustomerRelease;
import com.bs.manage.model.json.Page;
import com.bs.manage.model.param.customer.CustomerReleaseSearchParam;
import com.bs.manage.service.common.CommonService;

/**
 * 2020/4/7 9:40
 * fzj
 */
public interface CustomerReleaseService extends CommonService<CustomerRelease> {

    /**
     * 分页查询待释放的客户
     *
     * @param param 查询参数
     * @return 待释放的客户列表
     */
    Page<CustomerRelease> getByPage(CustomerReleaseSearchParam param);

    /**
     * 清空表
     */
    void truncate();
}
