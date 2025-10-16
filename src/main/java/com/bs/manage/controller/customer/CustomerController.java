package com.bs.manage.controller.customer;

import com.bs.manage.annotation.Role;
import com.bs.manage.model.bean.customer.Customer;
import com.bs.manage.model.bean.customer.CustomerShop;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.model.param.customer.BatchAttachParam;
import com.bs.manage.model.param.customer.BatchTagParam;
import com.bs.manage.model.param.customer.CustomerSearchParam;
import com.bs.manage.service.customer.CustomerService;
import com.bs.manage.service.customer.CustomerTagRelationService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


/**
 * 2020/1/16 11:21
 * fzj
 * 客户池控制器
 */
@RestController
@Validated
@RequestMapping("admin")
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerTagRelationService customerTagRelationService;


    public CustomerController(CustomerService customerService, CustomerTagRelationService customerTagRelationService) {
        this.customerService = customerService;
        this.customerTagRelationService = customerTagRelationService;
    }

    /**
     * 录入客户
     *
     * @param customer 详见customer
     * @return 新增后的客户信息
     */
    @PostMapping("customer")
    @Role
    public ResponseJson insert(@RequestBody Customer customer) {
        return customerService.insert(customer);
    }

    /**
     * 导入客户
     *
     * @param file excel
     * @return 导入结果
     */
    @PostMapping("customer/import")
    @Role
    public ResponseJson customerImport(@RequestParam("file") MultipartFile file) {
        return customerService.customerImport(file);
    }

    /**
     * 客户导出到excel
     */
    @RequestMapping("customer/download")
    @Role
    public void customerDownload(CustomerSearchParam param, HttpServletResponse response) {
        customerService.customerDownload(param, response);
    }

    /**
     * 删除客户
     *
     * @param id 客户id
     * @return 是否删除成功
     */
    @DeleteMapping("customer/{id}")
    @Role
    public ResponseJson delete(@PathVariable("id") Long id) {
        return customerService.delete(id);
    }

    /**
     * @param id 客户id
     * @return 更新后的客户信息
     */
    @PutMapping("customer/{id}")
    @Role
    public ResponseJson update(@PathVariable("id") Long id, @RequestBody Customer customer) {
        customer.setId(id);
        return customerService.update(customer);
    }

    /**
     * 批量标签管理(批量打标)
     * 多个用户打相同的多个标签
     * <p>
     * ids  客户id数组
     * tags 标签id数组
     *
     * @return 是否打标签成功
     */
    @PostMapping("customer/batch_attach_tags")
    public ResponseJson batchAttachTags(@RequestBody BatchTagParam param) {
        return customerTagRelationService.batchAttachTags(param);
    }

    /**
     * 单个标签管理
     * 单个用户分离标签(即删除一个标签)
     *
     * @param customer_id 客户id
     * @param tag_id      标签id
     * @return 是否分离标签成功
     */
    @PostMapping("customer/{customer_id}/detach_tag/{tag_id}")
    public ResponseJson detachTag(@PathVariable("customer_id") Long customer_id, @PathVariable("tag_id") Long tag_id) {
        return customerTagRelationService.detachTag(customer_id, tag_id);
    }

    /**
     * 批量客户绑定(批量分配)
     * 多个客户绑定同一个客户并指定项目,即跟进多个客户
     * <p>
     * ids        客户id数组
     * user_id    账号id
     * project_id 项目id
     *
     * @return 是否分配成功
     */
    @PostMapping("customer/batch_attach_user")
    @Role
    public ResponseJson batchAttachUser(@RequestBody BatchAttachParam param) {
        return customerService.batchAttachUser(param);
    }

    /**
     * 单个客户解绑
     * 登入用户删除当前跟进人
     *
     * @param customer_id 客户id
     * @param is_release  是否是释放
     * @return 是否解绑成功
     */
    @PostMapping("customer/{customer_id}/detach_user")
    public ResponseJson detachUser(@PathVariable("customer_id") Long customer_id, Integer is_release) {
        return customerService.detachUser(customer_id, is_release);
    }

    /**
     * 分页查询客户列表
     *
     * @param param 查询参数
     * @return 客户列表
     */
    @GetMapping("customers")
    @Role
    public ResponseJson getByPage(@Validated CustomerSearchParam param) {
        return ResponseJson.success(customerService.getByPage(param));
    }

    /**
     * 查询客户是否已存在重复
     *
     * @param keyword 客户名称或唯一ID
     * @return 客户列表
     */
    @GetMapping("customer/judge_repeat")
    public ResponseJson judgeRepeat(@NotBlank String keyword) {
        return customerService.judgeRepeat(keyword);
    }


    /**
     * 客户详细信息
     *
     * @param id     达人id
     * @param limit  日报分页最大条数
     * @param offset 日报分页第几条开始
     * @return 单个客户详细信息
     */
    @GetMapping("customer/{id}/process_all")
    public ResponseJson processAll(@PathVariable("id") Long id, @NotNull Integer limit, @NotNull Integer offset) {
        return ResponseJson.success(customerService.processAll(id, limit, offset));
    }


    @GetMapping("customer/configure")
    public ResponseJson getCustomerConfigure() {
        return ResponseJson.success(customerService.getCustomerConfigure());
    }


    /**
     * 所有店铺
     *
     * @param parentUniqueId 客户的唯一识别
     * @return 店铺列表
     */
    @GetMapping("customer/getAllShop")
    public ResponseJson getAllShop(@NotBlank String parentUniqueId) {
        return customerService.getAllShop(parentUniqueId);
    }

    /**
     * 店铺详情
     *
     * @param customer_shop_id 店铺的id
     * @return 店铺详情
     */
    @GetMapping("customer/getShopDetail")
    public ResponseJson getShopDetail(@NotNull Long customer_shop_id) {
        return customerService.getShopDetail(customer_shop_id);
    }

    @PutMapping("customer/updateShop/{customer_shop_id}")
    public ResponseJson updateShop(@PathVariable("customer_shop_id") Long customer_shop_id, @RequestBody CustomerShop customerShop) {
        customerShop.setId(customer_shop_id);
        return customerService.updateShop(customerShop);
    }

    @DeleteMapping("deleteShop/{customer_shop_id}")
    public ResponseJson deleteShop(@PathVariable("customer_shop_id") Long customer_shop_id) {
        return customerService.deleteShop(customer_shop_id);
    }

    /**
     * 升级店铺
     *
     * @param customer_shop_id 店铺的id
     * @return 升级结果
     */
    @PutMapping("customer/upgradeShop/{customer_shop_id}")
    public ResponseJson upgradeShop(@PathVariable("customer_shop_id") Long customer_shop_id) {
        return customerService.upgradeShop(customer_shop_id);
    }
}
