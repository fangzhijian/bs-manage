package com.bs.manage.controller.product;

import com.bs.manage.annotation.Role;
import com.bs.manage.constant.RedisConstants;
import com.bs.manage.model.bean.product.ProductProject;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.service.product.ProductProjectService;
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
import jakarta.validation.constraints.NotNull;

/**
 * 2020/3/2 10:15
 * fzj
 * 产品项目
 */
@RestController
@RequestMapping("admin/product_project")
@Validated
@Role(isAdmin = true)
public class ProductProjectController {

    private final ProductProjectService productProjectService;

    public ProductProjectController(ProductProjectService productProjectService) {
        this.productProjectService = productProjectService;
    }

    /**
     * @param name             产品项目名称
     * @param product_brand_id 产品品牌id
     * @param is_major         是否重点项目 0-否 1-是
     * @return 增加结果
     */
    @PostMapping
    @CacheEvict(cacheNames = RedisConstants.PRODUCT_PROJECT, allEntries = true)
    public ResponseJson inset(@NotBlank String name, @NotNull Long product_brand_id, Integer is_major) {
        return productProjectService.insert(ProductProject.builder().name(name).product_brand_id(product_brand_id).is_major(is_major == null ? 0 : is_major).build());
    }

    @DeleteMapping("{id}")
    @CacheEvict(cacheNames = RedisConstants.PRODUCT_PROJECT, allEntries = true)
    public ResponseJson delete(@PathVariable("id") Long id) {
        return productProjectService.delete(id);
    }

    /**
     * @param id               产品项目id
     * @param name             产品项目名称
     * @param product_brand_id 产品品牌id
     * @param is_major         是否重点项目 0-否 1-是
     * @return 修改结果
     */
    @PutMapping("{id}")
    @CacheEvict(cacheNames = RedisConstants.PRODUCT_PROJECT, allEntries = true)
    public ResponseJson update(@PathVariable("id") Long id, @NotBlank String name, @NotNull Long product_brand_id, Integer is_major) {
        return productProjectService.update(ProductProject.builder().id(id).product_brand_id(product_brand_id).name(name).is_major(is_major == null ? 0 : is_major).build());
    }

    @GetMapping
    public ResponseJson getAll() {
        return ResponseJson.success(productProjectService.getAll());
    }
}
