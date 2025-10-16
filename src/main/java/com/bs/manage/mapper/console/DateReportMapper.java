package com.bs.manage.mapper.console;

import com.bs.manage.mapper.common.CommonMapper;
import com.bs.manage.model.bean.console.DateReport;
import com.bs.manage.model.bean.console.IdAndAmount;
import com.bs.manage.model.bean.console.IdAndCount;
import com.bs.manage.model.bean.console.ReportSummary;
import com.bs.manage.model.param.console.DateReportSearchParam;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


/**
 * 2020/3/4 12:01
 * fzj
 */
@Repository
public interface DateReportMapper extends CommonMapper<DateReport> {

    Integer countByPage(DateReportSearchParam param);

    List<DateReport> getByPage(DateReportSearchParam param);

    /**
     * 根据id得到不同的统计
     *
     * @param goal_month 目标月份
     * @param groupId    要统计的id
     * @param userIds    账号id列表
     * @param monthEnd   查询月份最大天数
     * @return 返回统计值
     */
    List<IdAndAmount> summaryGroupId(@Param("goal_month") Integer goal_month, @Param("groupId") String groupId, @Param("userIds") List<Long> userIds,
                                     @Param("monthEnd") Integer monthEnd);

    /**
     * 统计覆盖数、拜访数、成交数、活跃数
     *
     * @param queryType 1-覆盖数 2-拜访数 3-成交数 4-活跃数
     * @param month     查询月份
     * @return 返回统计值
     */
    List<IdAndCount> summaryByType(@Param("queryType") Integer queryType, @Param("month") Integer month);

    /**
     * 获取前一条交易时间
     *
     * @param id          删除的日报id
     * @param customer_id 客户id
     * @return 前一条交易时间
     */
    LocalDateTime getPrevTimeWhenDelete(@Param("id") Long id, @Param("customer_id") Long customer_id);

    /**
     * 获取客单价
     *
     * @param userIds   账号id列表
     * @param queryType 1-团队 2-客户
     * @param month     查询月份
     * @param monthEnd  查询月的最大天数
     * @return 客单价
     */
    List<IdAndAmount> getUnitPrice(@Param("queryType") Integer queryType, @Param("userIds") List<Long> userIds, @Param("month") Integer month, @Param("monthEnd") Integer monthEnd);


    /**
     * 统计拜访行为情况
     *
     * @param userIds   账号id列表
     * @param queryType 1-团队 2-客户
     * @param date      查询日期
     * @return 返回统计值
     */
    List<ReportSummary> summaryAction(@Param("userIds") List<Long> userIds, @Param("queryType") Integer queryType, @Param("date") Integer date);


    /**
     * 销售排行
     *
     * @param month    查询月
     * @param monthEnd 查询月最大天数
     * @param team_id  团队id
     * @return 排行数据
     */
    List<IdAndAmount> topSale(@Param("month") Integer month, @Param("monthEnd") Integer monthEnd, @Param("team_id") Long team_id);

    /**
     * 分组统计日报数与客户数
     *
     * @param userIds   账号id列表
     * @param queryType 1-团队 2-客户
     * @param month     查询月份
     * @param monthEnd  查询月的最大天数
     * @return 日报个数
     */
    List<IdAndCount> getCountIdByGroupId(@Param("queryType") Integer queryType, @Param("userIds") List<Long> userIds, @Param("month") Integer month, @Param("monthEnd") Integer monthEnd);

    /**
     * 获取有成交的客户去重复的日报列表
     *
     * @param userIds  账号id列表
     * @param month    查询月份
     * @param monthEnd 查询月的最大天数
     * @return 日报列表
     */
    List<DateReport> distinctCustomerIdForDeal(@Param("userIds") List<Long> userIds, @Param("month") Integer month, @Param("monthEnd") Integer monthEnd);
}
