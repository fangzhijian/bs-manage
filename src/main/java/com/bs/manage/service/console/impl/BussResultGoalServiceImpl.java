package com.bs.manage.service.console.impl;

import com.bs.manage.mapper.console.BussResultGoalMapper;
import com.bs.manage.model.bean.console.BussResultGoal;
import com.bs.manage.service.common.impl.CommonServiceImpl;
import com.bs.manage.service.console.BussResultGoalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 2020/3/12 19:19
 * fzj
 */
@Service
public class BussResultGoalServiceImpl extends CommonServiceImpl<BussResultGoal> implements BussResultGoalService {

    private final BussResultGoalMapper bussResultGoalMapper;

    public BussResultGoalServiceImpl(BussResultGoalMapper bussResultGoalMapper) {
        this.bussResultGoalMapper = bussResultGoalMapper;
    }

    @Override
    public void afterPropertiesSet() {
        setCommonMapper(bussResultGoalMapper);
    }

    /**
     * 批量插入或更新
     *
     * @param list 过程目标列表
     */
    @Override
    @Transactional
    public void insertDuplicateBatch(List<BussResultGoal> list) {
        LocalDateTime now = LocalDateTime.now();
        for (BussResultGoal bussResultGoal : list) {
            bussResultGoal.setCreated_at(now);
            bussResultGoal.setUpdated_at(now);
        }
        bussResultGoalMapper.insertDuplicateBatch(list);
    }

    /**
     * 根据id获取目标结果
     *
     * @param goal_month 目标月份
     * @param userIds    账号id列表
     * @return 目标结果
     */
    @Override
    public List<BussResultGoal> getListByUserIds(Integer goal_month, List<Long> userIds) {
        return bussResultGoalMapper.getListByUserIds(goal_month, userIds);
    }

    /**
     * 根据条件查询目标结果总金额
     *
     * @param bussResultGoal 条件信息
     * @return 总金额
     */
    @Override
    public BigDecimal getTotalMoneyBySelectKey(BussResultGoal bussResultGoal) {
        return bussResultGoalMapper.getTotalMoneyBySelectKey(bussResultGoal);
    }
}
