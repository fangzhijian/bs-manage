package com.bs.manage.controller.product;

import com.bs.manage.annotation.Role;
import com.bs.manage.constant.RedisConstants;
import com.bs.manage.model.bean.product.Product;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.model.param.product.ProductSearchParam;
import com.bs.manage.service.product.ProductService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 2020/3/2 10:15
 * fzj
 * 产品
 */
@RestController
@RequestMapping("admin/product")
@Role(isAdmin = true)
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @CacheEvict(cacheNames = RedisConstants.PRODUCT, allEntries = true)
    public ResponseJson inset(@Validated @RequestBody Product product) {
        return productService.insert(product);
    }

    @DeleteMapping("{id}")
    @CacheEvict(cacheNames = RedisConstants.PRODUCT, allEntries = true)
    public ResponseJson delete(@PathVariable("id") Long id) {
        return productService.delete(id);
    }

    @PutMapping("{id}")
    @CacheEvict(cacheNames = RedisConstants.PRODUCT, allEntries = true)
    public ResponseJson update(@PathVariable("id") Long id, @Validated @RequestBody Product product) {
        product.setId(id);
        return productService.update(product);
    }

    @GetMapping
    @Role(except = true)
    @Cacheable(cacheNames = RedisConstants.PRODUCT, key = "#param.getOffset()")
    public ResponseJson getByPage(@Validated ProductSearchParam param) {
        //先不分页,产品数量少
        param.setLimit(1000);
        return ResponseJson.success(productService.getByPage(param));
    }
}
