package com.bs.manage.controller.customer;

import com.bs.manage.annotation.Role;
import com.bs.manage.constant.RedisConstants;
import com.bs.manage.model.bean.customer.ProductNation;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.service.customer.ProductNationService;
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
 * 2020/1/16 10:10
 * fzj
 * 客户相关配置-主营产品国籍控制器
 */
@RestController
@Validated
@RequestMapping("admin/customer/product/nation")
@Role(isAdmin = true)
public class ProductNationController {

    private final ProductNationService productNationService;

    public ProductNationController(ProductNationService productNationService) {
        this.productNationService = productNationService;
    }

    @PostMapping
    @CacheEvict(cacheNames = RedisConstants.PRODUCT_NATION, allEntries = true)
    public ResponseJson insert(@NotBlank String name) {
        ProductNation productNation = ProductNation.builder().name(name).build();
        productNationService.insert(productNation);
        return ResponseJson.success(productNation);
    }

    @DeleteMapping("{id}")
    @CacheEvict(cacheNames = RedisConstants.PRODUCT_NATION, allEntries = true)
    public ResponseJson delete(@PathVariable("id") Long id) {
        return productNationService.delete(id);
    }

    @PutMapping("{id}")
    @CacheEvict(cacheNames = RedisConstants.PRODUCT_NATION, allEntries = true)
    public ResponseJson update(@PathVariable("id") Long id, @NotBlank String name) {
        return productNationService.update(ProductNation.builder().id(id).name(name).build());
    }

    @GetMapping
    public ResponseJson getAll() {
        return ResponseJson.success(productNationService.getAll());
    }
}
