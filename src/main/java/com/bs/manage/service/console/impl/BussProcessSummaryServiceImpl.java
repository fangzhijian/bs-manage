package com.bs.manage.service.console.impl;

import com.bs.manage.mapper.console.BussProcessSummaryMapper;
import com.bs.manage.model.bean.console.BussProcessSummary;
import com.bs.manage.service.common.impl.CommonServiceImpl;
import com.bs.manage.service.console.BussProcessSummaryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 2020/3/12 19:18
 * fzj
 */
@Service
public class BussProcessSummaryServiceImpl extends CommonServiceImpl<BussProcessSummary> implements BussProcessSummaryService {

    private final BussProcessSummaryMapper bussProcessSummaryMapper;

    public BussProcessSummaryServiceImpl(BussProcessSummaryMapper bussProcessSummaryMapper) {
        this.bussProcessSummaryMapper = bussProcessSummaryMapper;
    }

    /**
     * 根据唯一所有批量更新
     *
     * @param list 业务过程统计集合
     */
    @Override
    @Transactional
    public void updateBatchByUnique(List<BussProcessSummary> list) {
        LocalDateTime now = LocalDateTime.now();
        for (BussProcessSummary bussProcessSummary : list) {
            bussProcessSummary.setUpdated_at(now);
        }
        bussProcessSummaryMapper.updateBatchByUnique(list);
    }

    /**
     * 统计覆盖数、拜访数、成交数、活跃数的后续操
     *
     * @param list 业务过程统计集合
     */
    @Override
    @Transactional
    public void afterSummary(List<BussProcessSummary> list) {
        LocalDateTime now = LocalDateTime.now();
        for (BussProcessSummary bussProcessSummary : list) {
            bussProcessSummary.setCreated_at(now);
            bussProcessSummary.setUpdated_at(now);
        }
        bussProcessSummaryMapper.insertDuplicateBatch(list);
    }

    @Override
    public void afterPropertiesSet() {
        setCommonMapper(bussProcessSummaryMapper);
    }
}
