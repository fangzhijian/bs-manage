package com.bs.manage.mapper.customer;

import com.bs.manage.mapper.common.CommonMapper;
import com.bs.manage.model.bean.customer.CustomerLevel;
import com.bs.manage.model.bean.customer.ProductCategory;
import org.springframework.stereotype.Repository;

/**
 * 2020/2/26 10:48
 * fzj
 */
@Repository
public interface ProductCategoryMapper extends CommonMapper<ProductCategory> {

    Boolean hasUsed(Long id);

}
