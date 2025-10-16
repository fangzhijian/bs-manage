package com.bs.manage.controller.customer;

import com.bs.manage.annotation.Role;
import com.bs.manage.constant.RedisConstants;
import com.bs.manage.model.bean.customer.ProductCategory;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.service.customer.ProductCategoryService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.NotBlank;

/**
 * 2020/1/15 16:56
 * fzj
 * 客户相关配置-主营产品类目控制器
 */
@RestController
@Validated
@RequestMapping("admin/customer/product/category")
@Role(isAdmin = true)
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;

    public ProductCategoryController(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    @PostMapping
    @CacheEvict(cacheNames = RedisConstants.PRODUCT_CATEGORY, allEntries = true)
    public ResponseJson insert(@NotBlank String name) {
        ProductCategory productCategory = ProductCategory.builder().name(name).build();
        return ResponseJson.success(productCategory);
    }

    @DeleteMapping("{id}")
    @CacheEvict(cacheNames = RedisConstants.PRODUCT_CATEGORY, allEntries = true)
    public ResponseJson delete(@PathVariable("id") Long id) {
        return productCategoryService.delete(id);
    }

    @PutMapping("{id}")
    @CacheEvict(cacheNames = RedisConstants.PRODUCT_CATEGORY, allEntries = true)
    public ResponseJson update(@PathVariable("id") Long id, @NotBlank String name) {
        return productCategoryService.update(ProductCategory.builder().id(id).name(name).build());
    }

    @GetMapping
    public ResponseJson getAll() {
        return ResponseJson.success(productCategoryService.getAll());
    }
}
