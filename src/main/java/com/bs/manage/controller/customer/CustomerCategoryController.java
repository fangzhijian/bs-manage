package com.bs.manage.controller.customer;

import com.bs.manage.constant.RedisConstants;
import com.bs.manage.model.bean.customer.CustomerCategory;
import com.bs.manage.model.json.MultiLabel;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.service.customer.CustomerCategoryService;
import com.bs.manage.until.NumberUtil;
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
import java.util.List;

/**
 * 2020/1/15 16:56
 * fzj
 * 客户属性管理控制器
 */
@RestController
@Validated
@RequestMapping("admin/customer/category")
public class CustomerCategoryController {

    private final CustomerCategoryService customerCategoryService;

    public CustomerCategoryController(CustomerCategoryService customerCategoryService) {
        this.customerCategoryService = customerCategoryService;
    }

    @PostMapping
    @CacheEvict(cacheNames = RedisConstants.CUSTOMER_CATEGORY, allEntries = true)
    public ResponseJson insert(Long parent_id, @NotBlank String name) {
        long parentId = NumberUtil.isBlank(parent_id) ? 0 : parent_id;
        CustomerCategory customerCategory = CustomerCategory.builder().parent_id(parentId).name(name).build();
        customerCategoryService.insert(customerCategory);
        return ResponseJson.success(customerCategory);
    }

    @DeleteMapping("{id}")
    @CacheEvict(cacheNames = RedisConstants.CUSTOMER_CATEGORY, allEntries = true)
    public ResponseJson delete(@PathVariable("id") Long id) {
        customerCategoryService.delete(id);
        return ResponseJson.success();
    }

    @PutMapping("{id}")
    @CacheEvict(cacheNames = RedisConstants.CUSTOMER_CATEGORY, allEntries = true)
    public ResponseJson update(@PathVariable("id") Long id, @NotBlank String name) {
        customerCategoryService.update(CustomerCategory.builder().id(id).name(name).build());
        return ResponseJson.success();
    }

    @GetMapping
    public ResponseJson getAll() {
        List<CustomerCategory> customerCategoryList = customerCategoryService.getAll();
        return ResponseJson.success(MultiLabel.getInstance(customerCategoryList));
    }
}
