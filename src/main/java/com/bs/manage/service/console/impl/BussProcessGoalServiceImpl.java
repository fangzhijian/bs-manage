package com.bs.manage.service.console.impl;

import com.bs.manage.mapper.console.BussProcessGoalMapper;
import com.bs.manage.model.bean.console.BussProcessGoal;
import com.bs.manage.service.common.impl.CommonServiceImpl;
import com.bs.manage.service.console.BussProcessGoalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 2020/3/12 19:16
 * fzj
 */
@Service
public class BussProcessGoalServiceImpl extends CommonServiceImpl<BussProcessGoal> implements BussProcessGoalService {

    private final BussProcessGoalMapper bussProcessGoalMapper;

    public BussProcessGoalServiceImpl(BussProcessGoalMapper bussProcessGoalMapper) {
        this.bussProcessGoalMapper = bussProcessGoalMapper;
    }

    @Override
    public void afterPropertiesSet() {
        setCommonMapper(bussProcessGoalMapper);
    }

    /**
     * 批量插入或更新
     *
     * @param list 过程目标列表
     * @return 改动的条数
     */
    @Override
    @Transactional
    public Integer insertDuplicateBatch(List<BussProcessGoal> list) {
        LocalDateTime now = LocalDateTime.now();
        for (BussProcessGoal processGoal : list) {
            processGoal.setCreated_at(now);
            processGoal.setUpdated_at(now);
        }
        return bussProcessGoalMapper.insertDuplicateBatch(list);
    }
}
