package com.bs.manage.controller.customer;

import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.model.param.customer.CustomerReleaseSearchParam;
import com.bs.manage.service.customer.CustomerReleaseService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 2020/4/7 9:48
 * fzj
 */
@RestController
@RequestMapping("admin/customer_release")
public class CustomerReleaseController {

    private final CustomerReleaseService customerReleaseService;

    public CustomerReleaseController(CustomerReleaseService customerReleaseService) {
        this.customerReleaseService = customerReleaseService;
    }

    /**
     * 分页查询待释放的客户
     *
     * @param param 查询参数
     * @return 待释放的客户列表
     */
    @GetMapping
    public ResponseJson getByPage(@Validated CustomerReleaseSearchParam param) {
        return ResponseJson.success(customerReleaseService.getByPage(param));
    }

}
