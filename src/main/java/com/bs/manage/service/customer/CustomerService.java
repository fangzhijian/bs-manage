package com.bs.manage.service.customer;

import com.bs.manage.model.bean.customer.Customer;
import com.bs.manage.model.bean.customer.CustomerRelease;
import com.bs.manage.model.bean.customer.CustomerShop;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.model.param.customer.BatchAttachParam;
import com.bs.manage.model.param.customer.CustomerSearchParam;
import com.bs.manage.service.common.CommonService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
 * 2020/2/27 10:44
 * fzj
 */
public interface CustomerService extends CommonService<Customer> {

    /**
     * 导入客户
     *
     * @param file excel
     */
    ResponseJson customerImport(MultipartFile file);

    /**
     * 导出客户
     *
     * @param param 查询参数
     */
    void customerDownload(CustomerSearchParam param, HttpServletResponse response);

    /**
     * 客户列表
     *
     * @param param 查询参数
     */
    ResponseJson getByPage(CustomerSearchParam param);

    /**
     * @param id     id
     * @param limit  日报分页最大条数
     * @param offset 日报分页第几条开始
     * @return 客户详细详细
     */
    ResponseJson processAll(Long id, Integer limit, Integer offset);


    /**
     * 获取配置
     */
    ResponseJson getCustomerConfigure();

    /**
     * 账号关联客户
     */
    ResponseJson batchAttachUser(BatchAttachParam param);

    /**
     * @param customer_id 客户id
     * @param is_release  是否是释放
     * @return 解除关联
     */
    ResponseJson detachUser(Long customer_id, Integer is_release);

    /**
     * 仅仅修改客户信息
     *
     * @param customer 客户信息
     */
    void updateOnly(Customer customer);

    /**
     * 查询所有需要统计活跃的客户日报数量
     *
     * @return 客户数量
     */
    Integer countByActiveCustomerDateReport();

    /**
     * 需要统计活跃的客户和日报
     *
     * @param offset 第几条开始
     * @param limit  每页条数
     * @return 客户和日报列表
     */
    List<Customer> getByActiveCustomerDateReport(Integer offset, Integer limit);


    /**
     * 需要释放的客户
     *
     * @param release_type       1-未拜访释放 2-未成交释放 3-未复购释放
     * @param customer_gradation 客户分层
     * @param left_day           待释放天数
     * @return 释放的客户
     */
    List<CustomerRelease> getByReleaseCustomer(Integer release_type, String customer_gradation, Integer left_day);

    /**
     * 客户释放的后续操作
     *
     * @param customerDateReportList 待释放的客户列表
     */
    void afterRelease(List<CustomerRelease> customerDateReportList);

    /**
     * 查询客户是否已存在重复
     *
     * @param keyword 客户名称或唯一ID
     * @return 客户列表
     */
    ResponseJson judgeRepeat(String keyword);


    ResponseJson getAllShop(String parentUniqueId);

    /**
     * 店铺详情
     *
     * @param customer_shop_id 店铺的id
     * @return 店铺详情
     */
    ResponseJson getShopDetail(Long customer_shop_id);

    ResponseJson updateShop(CustomerShop customerShop);

    ResponseJson deleteShop(Long customer_shop_id);

    /**
     * 升级店铺
     *
     * @param customer_shop_id 店铺的id
     * @return 升级结果
     */
    ResponseJson upgradeShop(Long customer_shop_id);

    /**
     * 释放所有客户
     *
     * @param userId 账号id
     */
    void releaseAllByUserId(Long userId);
}
