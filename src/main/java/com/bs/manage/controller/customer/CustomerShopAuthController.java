package com.bs.manage.controller.customer;

import com.bs.manage.annotation.Role;
import com.bs.manage.model.bean.customer.CustomerShopAuth;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.service.customer.CustomerShopAuthService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 2020/4/15 9:23
 * fzj
 */
@RestController
@RequestMapping("admin/customer_shop/auth")
@Validated
@Role(roleId = 9)
public class CustomerShopAuthController {

    private final CustomerShopAuthService customerShopAuthService;

    public CustomerShopAuthController(CustomerShopAuthService customerShopAuthService) {
        this.customerShopAuthService = customerShopAuthService;
    }

    @PostMapping()
    public ResponseJson insert(@Validated CustomerShopAuth customerAuth) {
        return customerShopAuthService.insert(customerAuth);
    }


    @DeleteMapping("{id}")
    public ResponseJson delete(@PathVariable("id") Long id) {
        return customerShopAuthService.delete(id);
    }

    @PutMapping("{id}")
    public ResponseJson update(@PathVariable("id") Long id, @Validated CustomerShopAuth customerAuth) {
        customerAuth.setId(id);
        customerAuth.setCustomer_shop_id(null);
        return customerShopAuthService.update(customerAuth);
    }
}
