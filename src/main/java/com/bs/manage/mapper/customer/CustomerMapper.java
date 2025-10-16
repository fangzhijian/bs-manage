package com.bs.manage.mapper.customer;

import com.bs.manage.mapper.common.CommonMapper;
import com.bs.manage.model.bean.customer.Customer;
import com.bs.manage.model.bean.customer.CustomerRelease;
import com.bs.manage.model.bean.customer.CustomerShop;
import com.bs.manage.model.param.customer.CustomerSearchParam;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 2020/2/27 10:42
 * fzj
 */
@Repository
public interface CustomerMapper extends CommonMapper<Customer> {

    String getMaxSn(Long customer_category_id);

    Integer countByPage(CustomerSearchParam param);

    List<Customer> getByPage(CustomerSearchParam param);

    List<Customer> getByIds(List<Long> list);

    Customer processAll(Long id);

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
    List<Customer> getByActiveCustomerDateReport(@Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 待释放的客户
     *
     * @param release_type       1-未拜访释放 2-未成交释放 3-未复购释放
     * @param customer_gradation 客户分层
     * @param left_day           待释放天数
     * @return 释放的客户
     */
    List<CustomerRelease> getByReleaseCustomer(@Param("release_type") Integer release_type, @Param("customer_gradation") String customer_gradation,
                                               @Param("left_day") Integer left_day);

    /**
     * 查询客户是否已存在重复
     *
     * @param keyword 客户名称或唯一ID
     * @param customerShops 店铺信息
     * @return 客户列表
     */
    List<Customer> judgeRepeat(@Param("keyword") String keyword, @Param("customerShops") List<CustomerShop> customerShops);

    /**
     * 查询一个账号已经绑定了多少个客户
     *
     * @param userId 账号id
     * @return 绑定的客户数
     */
    Integer countBindCustomer(Long userId);

    /**
     * 释放所有客户
     *
     * @param userId 账号id
     */
    void releaseAllByUserId(Long userId);
}
