package com.bs.manage.mapper.customer;

import com.bs.manage.mapper.common.CommonMapper;
import com.bs.manage.model.bean.customer.CustomerRelease;
import com.bs.manage.model.param.customer.CustomerReleaseSearchParam;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 2020/4/7 9:39
 * fzj
 */
@Repository
public interface CustomerReleaseMapper extends CommonMapper<CustomerRelease> {

    Integer countByPage(CustomerReleaseSearchParam param);

    List<CustomerRelease> getByPage(CustomerReleaseSearchParam param);

    /**
     * 清空表
     */
    void truncate();
}
