package com.bs.manage.service.product.impl;

import com.bs.manage.constant.RedisConstants;
import com.bs.manage.mapper.product.ProductBrandMapper;
import com.bs.manage.model.bean.product.Product;
import com.bs.manage.model.bean.product.ProductBrand;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.service.common.impl.CommonServiceImpl;
import com.bs.manage.service.product.ProductBrandService;
import com.bs.manage.service.product.ProductService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 2020/3/2 11:01
 * fzj
 */
@Service
public class ProductBrandServiceImpl extends CommonServiceImpl<ProductBrand> implements ProductBrandService {

    private final ProductBrandMapper productBrandMapper;
    private final ProductService productService;

    public ProductBrandServiceImpl(ProductBrandMapper productBrandMapper, @Lazy ProductService productService) {
        this.productBrandMapper = productBrandMapper;
        this.productService = productService;
    }


    @Override
    @Transactional
    public ResponseJson insert(ProductBrand bean) {
        if (super.getOneBySelectKey(ProductBrand.builder().name(bean.getName()).build()) != null) {
            return ResponseJson.fail("品牌名称已存在");
        }
        return super.insert(bean);
    }

    @Override
    @Transactional
    public ResponseJson update(ProductBrand bean) {
        ProductBrand productBrand = super.getOneBySelectKey(ProductBrand.builder().name(bean.getName()).build());
        if (productBrand != null && !productBrand.getId().equals(bean.getId())) {
            return ResponseJson.fail("品牌名称已存在");
        }
        return super.update(bean);
    }

    @Override
    @Transactional
    public ResponseJson delete(Long id) {
        if (productService.getOneBySelectKey(Product.builder().product_brand_id(id).build()) != null) {
            return ResponseJson.fail("品牌已被使用 无法删除");
        }
        return super.delete(id);
    }

    @Override
    @Cacheable(cacheNames = RedisConstants.PRODUCT_BRAND)
    public List<ProductBrand> getAll() {
        return super.getAll();
    }

    @Override
    public void afterPropertiesSet() {
        setCommonMapper(productBrandMapper);
    }
}
