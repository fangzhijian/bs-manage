package com.bs.manage.service.product.impl;

import com.bs.manage.constant.RedisConstants;
import com.bs.manage.mapper.product.ProductProjectMapper;
import com.bs.manage.model.bean.product.Product;
import com.bs.manage.model.bean.product.ProductProject;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.service.common.impl.CommonServiceImpl;
import com.bs.manage.service.product.ProductProjectService;
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
public class ProductProjectServiceImpl extends CommonServiceImpl<ProductProject> implements ProductProjectService {

    private final ProductProjectMapper productProjectMapper;
    private final ProductService productService;

    public ProductProjectServiceImpl(ProductProjectMapper productProjectMapper, @Lazy ProductService productService) {
        this.productProjectMapper = productProjectMapper;
        this.productService = productService;
    }


    @Override
    @Transactional
    public ResponseJson insert(ProductProject bean) {
        if (super.getOneBySelectKey(ProductProject.builder().name(bean.getName()).build()) != null) {
            return ResponseJson.fail("项目名称已存在");
        }
        return super.insert(bean);
    }

    @Override
    @Transactional
    public ResponseJson delete(Long id) {
        if (productService.getOneBySelectKey(Product.builder().product_project_id(id).build()) != null) {
            return ResponseJson.fail("项目已被使用 无法删除");
        }
        return super.delete(id);
    }

    @Override
    @Transactional
    public ResponseJson update(ProductProject bean) {
        ProductProject productProject = super.getOneBySelectKey(ProductProject.builder().name(bean.getName()).build());
        if (productProject != null && !productProject.getId().equals(bean.getId())) {
            return ResponseJson.fail("项目名称已存在");
        }
        ProductProject oldProject = super.getById(bean.getId());
        //修改项目下面的品牌id
        if (!bean.getProduct_brand_id().equals(oldProject.getProduct_brand_id())) {
            productService.updateAsBrandId(bean.getProduct_brand_id(), oldProject.getId());
        }
        return super.update(bean);
    }

    @Override
    @Cacheable(cacheNames = RedisConstants.PRODUCT_PROJECT)
    public List<ProductProject> getAll() {
        return super.getAll();
    }

    @Override
    public void afterPropertiesSet() {
        setCommonMapper(productProjectMapper);
    }
}
