package com.bs.manage.service.console;

import com.bs.manage.model.bean.console.BussResultGoal;
import com.bs.manage.service.common.CommonService;

import java.math.BigDecimal;
import java.util.List;

/**
 * 2020/3/12 19:13
 * fzj
 */
public interface BussResultGoalService extends CommonService<BussResultGoal> {

    /**
     * 批量插入或更新
     *
     * @param list 过程目标列表
     */
    void insertDuplicateBatch(List<BussResultGoal> list);

    /**
     * 根据id获取目标结果
     *
     * @param goal_month 目标月份
     * @param userIds    账号id列表
     * @return 目标结果
     */
    List<BussResultGoal> getListByUserIds(Integer goal_month, List<Long> userIds);


    /**
     * 根据条件查询目标结果总金额
     *
     * @param bussResultGoal 条件信息
     * @return 总金额
     */
    BigDecimal getTotalMoneyBySelectKey(BussResultGoal bussResultGoal);
}
