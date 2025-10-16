package com.bs.manage.controller.customer;

import com.bs.manage.annotation.Role;
import com.bs.manage.constant.RedisConstants;
import com.bs.manage.model.bean.customer.CustomerLevel;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.service.customer.CustomerLevelService;
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
 * 2020/1/15 16:29
 * fzj
 * 客户相关配置-客户等级管理控制器
 */
@RestController
@RequestMapping("/admin/customer/level")
@Validated
@Role(isAdmin = true)
public class CustomerLevelController {

    private final CustomerLevelService customerLevelService;

    public CustomerLevelController(CustomerLevelService customerLevelService) {
        this.customerLevelService = customerLevelService;
    }

    @PostMapping
    @CacheEvict(cacheNames = RedisConstants.CUSTOMER_LEVEL, allEntries = true)
    public ResponseJson insert(@NotBlank String name) {
        CustomerLevel customerLevel = CustomerLevel.builder().name(name).build();
        customerLevelService.insert(customerLevel);
        return ResponseJson.success(customerLevel);
    }

    @DeleteMapping("{id}")
    @CacheEvict(cacheNames = RedisConstants.CUSTOMER_LEVEL, allEntries = true)
    public ResponseJson delete(@PathVariable("id") Long id) {
        return customerLevelService.delete(id);
    }

    @PutMapping("{id}")
    @CacheEvict(cacheNames = RedisConstants.CUSTOMER_LEVEL, allEntries = true)
    public ResponseJson update(@PathVariable("id") Long id, @NotBlank String name) {
        return customerLevelService.update(CustomerLevel.builder().id(id).name(name).build());
    }

    @GetMapping
    public ResponseJson getAll() {
        return ResponseJson.success(customerLevelService.getAll());
    }
}
