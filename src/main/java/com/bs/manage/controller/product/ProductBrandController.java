package com.bs.manage.controller.product;

import com.bs.manage.annotation.Role;
import com.bs.manage.constant.RedisConstants;
import com.bs.manage.model.bean.product.ProductBrand;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.service.product.ProductBrandService;
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
 * 2020/3/2 10:15
 * fzj
 * 产品品牌
 */
@RestController
@RequestMapping("admin/product_brand")
@Validated
@Role(isAdmin = true)
public class ProductBrandController {

    private final ProductBrandService productBrandService;

    public ProductBrandController(ProductBrandService productBrandService) {
        this.productBrandService = productBrandService;
    }

    @PostMapping
    @CacheEvict(cacheNames = RedisConstants.PRODUCT_BRAND, allEntries = true)
    public ResponseJson inset(@NotBlank String name) {
        return productBrandService.insert(ProductBrand.builder().name(name).build());
    }

    @DeleteMapping("{id}")
    @CacheEvict(cacheNames = RedisConstants.PRODUCT_BRAND, allEntries = true)
    public ResponseJson delete(@PathVariable("id") Long id) {
        return productBrandService.delete(id);
    }

    @PutMapping("{id}")
    @CacheEvict(cacheNames = RedisConstants.PRODUCT_BRAND, allEntries = true)
    public ResponseJson update(@PathVariable("id") Long id, @NotBlank String name) {
        return productBrandService.update(ProductBrand.builder().id(id).name(name).build());
    }

    @GetMapping
    @Role(except = true)
    public ResponseJson getAll() {
        return ResponseJson.success(productBrandService.getAll());
    }
}
