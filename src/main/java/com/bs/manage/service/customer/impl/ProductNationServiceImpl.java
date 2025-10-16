package com.bs.manage.service.customer.impl;

import com.bs.manage.constant.RedisConstants;
import com.bs.manage.mapper.customer.ProductNationMapper;
import com.bs.manage.model.bean.customer.ProductNation;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.service.common.impl.CommonServiceImpl;
import com.bs.manage.service.customer.ProductNationService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 2020/2/26 11:04
 * fzj
 */
@Service
public class ProductNationServiceImpl extends CommonServiceImpl<ProductNation> implements ProductNationService {

    private final ProductNationMapper productNationMapper;

    public ProductNationServiceImpl(ProductNationMapper productNationMapper) {
        this.productNationMapper = productNationMapper;
    }

    @Override
    @Transactional
    public ResponseJson delete(Long id) {
        if (productNationMapper.hasUsed(id)) {
            ResponseJson.fail("该主营产品国籍已被使用,无法删除");
        }
        return super.delete(id);
    }

    @Override
    @Cacheable(cacheNames = RedisConstants.PRODUCT_NATION)
    public List<ProductNation> getAll() {
        return super.getAll();
    }

    @Override
    public void afterPropertiesSet() {
        setCommonMapper(productNationMapper);
    }
}
