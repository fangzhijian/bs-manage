package com.bs.manage.mapper.console;

import com.bs.manage.mapper.common.CommonMapper;
import com.bs.manage.model.bean.console.BussResultGoal;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 2020/3/12 11:12
 * fzj
 */
@Repository
public interface BussResultGoalMapper extends CommonMapper<BussResultGoal> {

    /**
     * 批量插入或更新
     *
     * @param list 过程目标列表
     * @return 改动的条数
     */
    Integer insertDuplicateBatch(List<BussResultGoal> list);

    /**
     * 根据id获取目标结果
     *
     * @param goal_month 目标月份
     * @param userIds    账号id列表
     * @return 目标结果
     */
    List<BussResultGoal> getListByUserIds(@Param("goal_month") Integer goal_month, @Param("userIds") List<Long> userIds);

    /**
     * 根据条件查询目标结果总金额
     *
     * @param bussResultGoal 条件信息
     * @return 总金额
     */
    BigDecimal getTotalMoneyBySelectKey(BussResultGoal bussResultGoal);
}
