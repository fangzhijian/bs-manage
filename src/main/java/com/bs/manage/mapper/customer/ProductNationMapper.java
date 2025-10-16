package com.bs.manage.mapper.customer;

import com.bs.manage.mapper.common.CommonMapper;
import com.bs.manage.model.bean.customer.ProductNation;
import org.springframework.stereotype.Repository;

/**
 * 2020/2/26 11:03
 * fzj
 */
@Repository
public interface ProductNationMapper extends CommonMapper<ProductNation> {

    Boolean hasUsed(Long id);

}
