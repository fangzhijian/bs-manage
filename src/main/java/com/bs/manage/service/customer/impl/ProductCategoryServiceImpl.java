package com.bs.manage.service.customer.impl;

import com.bs.manage.constant.RedisConstants;
import com.bs.manage.mapper.customer.ProductCategoryMapper;
import com.bs.manage.model.bean.customer.ProductCategory;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.service.common.impl.CommonServiceImpl;
import com.bs.manage.service.customer.ProductCategoryService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 2020/2/26 11:03
 * fzj
 */
@Service
public class ProductCategoryServiceImpl extends CommonServiceImpl<ProductCategory> implements ProductCategoryService {

    private final ProductCategoryMapper productCategoryMapper;

    public ProductCategoryServiceImpl(ProductCategoryMapper productCategoryMapper) {
        this.productCategoryMapper = productCategoryMapper;
    }

    @Override
    @Transactional
    public ResponseJson delete(Long id) {
        if (productCategoryMapper.hasUsed(id)) {
            ResponseJson.fail("该主营产品类目已被使用,无法删除");
        }
        return super.delete(id);
    }

    @Override
    @Cacheable(cacheNames = RedisConstants.PRODUCT_CATEGORY)
    public List<ProductCategory> getAll() {
        return super.getAll();
    }

    @Override
    public void afterPropertiesSet() {
        setCommonMapper(productCategoryMapper);
    }
}
