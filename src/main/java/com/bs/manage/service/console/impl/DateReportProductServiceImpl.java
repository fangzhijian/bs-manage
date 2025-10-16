package com.bs.manage.service.console.impl;

import com.bs.manage.mapper.console.DateReportProductMapper;
import com.bs.manage.model.bean.console.DateReportProduct;
import com.bs.manage.service.common.impl.CommonServiceImpl;
import com.bs.manage.service.console.DateReportProductService;
import org.springframework.stereotype.Service;

/**
 * 2020/3/13 16:43
 * fzj
 */
@Service
public class DateReportProductServiceImpl extends CommonServiceImpl<DateReportProduct> implements DateReportProductService {

    private final DateReportProductMapper dateReportProductMapper;

    public DateReportProductServiceImpl(DateReportProductMapper dateReportProductMapper) {
        this.dateReportProductMapper = dateReportProductMapper;
    }

    @Override
    public void afterPropertiesSet() {
        setCommonMapper(dateReportProductMapper);
    }

    /**
     * 根据日报id删除
     *
     * @param dateReportId 日报id
     * @return 删除结果
     */
    @Override
    public Integer deleteByDateReportId(Long dateReportId) {
        return dateReportProductMapper.deleteByDateReportId(dateReportId);
    }

    /**
     * 日报中是否含有产品
     *
     * @param product_id 产品id
     * @return 含有结果
     */
    @Override
    public Boolean existProduct(Long product_id) {
        return dateReportProductMapper.existProduct(product_id);
    }
}
