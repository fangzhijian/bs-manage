package com.bs.manage.service.console;

import com.bs.manage.model.bean.console.DateReport;
import com.bs.manage.model.bean.console.IdAndAmount;
import com.bs.manage.model.bean.console.IdAndCount;
import com.bs.manage.model.bean.console.ReportSummary;
import com.bs.manage.model.bean.console.TopUser;
import com.bs.manage.model.bean.customer.Customer;
import com.bs.manage.model.json.Page;
import com.bs.manage.model.param.console.BoardParam;
import com.bs.manage.model.param.console.DateReportSearchParam;
import com.bs.manage.service.common.CommonService;

import java.util.List;

/**
 * 2020/3/4 12:05
 * fzj
 */
public interface DateReportService extends CommonService<DateReport> {

    Page<DateReport> getByPage(DateReportSearchParam param);

    /**
     * 查询当前账号拥有的客户
     *
     * @return 当前账号拥有的客户
     */
    List<Customer> getOwmCustomer();


    /**
     * 根据id得到不同的统计
     *
     * @param goal_month 目标月份
     * @param groupId    要统计的id
     * @param userIds    账号id列表
     * @param monthEnd   查询月份最大天数
     * @return 返回统计值
     */
    List<IdAndAmount> summaryGroupId(Integer goal_month, String groupId, List<Long> userIds, Integer monthEnd);

    /**
     * 统计覆盖数、拜访数、成交数、活跃数
     *
     * @param queryType 1-覆盖数 2-拜访数 3-成交数 4-活跃数
     * @param month     查询月份
     * @return 返回统计值
     */
    List<IdAndCount> summaryByType(Integer queryType, Integer month);


    /**
     * 获取客单价
     *
     * @param userIds   账号id列表
     * @param queryType 1-团队 2-客户
     * @param month     查询月份
     * @param monthEnd  查询月的最大天数
     * @return 客单价
     */
    List<IdAndAmount> getUnitPrice(Integer queryType, List<Long> userIds, Integer month, Integer monthEnd);

    /**
     * 统计拜访行为情况
     *
     * @param userIds   账号id列表
     * @param queryType 1-团队 2-客户
     * @param date      查询日期
     * @return 返回统计值
     */
    List<ReportSummary> summaryAction(List<Long> userIds, Integer queryType, Integer date);


    /**
     * 销售排行
     *
     * @param param 入参
     * @return 销售排行信息
     */
    Page<TopUser> topSale(BoardParam param);

    /**
     * 分组统计日报数与客户数
     *
     * @param userIds   账号id列表
     * @param queryType 1-团队 2-客户
     * @param month     查询月份
     * @param monthEnd  查询月最大天数
     * @return 客户个数
     */
    List<IdAndCount> getCountIdByGroupId(Integer queryType, List<Long> userIds, Integer month, Integer monthEnd);

    /**
     * 获取有成交的客户去重复的日报列表
     *
     * @param userIds  账号id列表
     * @param month    查询月份
     * @param monthEnd 查询月的最大天数
     * @return 日报列表
     */
    List<DateReport> distinctCustomerIdForDeal(List<Long> userIds, Integer month, Integer monthEnd);


}
