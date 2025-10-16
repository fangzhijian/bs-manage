package com.bs.manage.service.erp.impl;

import com.bs.manage.mapper.erp.ReturnWarehouseEntryMapper;
import com.bs.manage.model.bean.erp.ReturnWarehouseEntry;
import com.bs.manage.service.common.impl.CommonServiceImpl;
import com.bs.manage.service.erp.ReturnWarehouseEntryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 2023/4/10 17:20
 * fzj
 */
@Service
public class ReturnWarehouseEntryServiceImpl extends CommonServiceImpl<ReturnWarehouseEntry> implements ReturnWarehouseEntryService {

    private final ReturnWarehouseEntryMapper returnWarehouseEntryMapper;

    public ReturnWarehouseEntryServiceImpl(ReturnWarehouseEntryMapper returnWarehouseEntryMapper) {
        this.returnWarehouseEntryMapper = returnWarehouseEntryMapper;
    }

    @Override
    public void afterPropertiesSet() {
        setCommonMapper(returnWarehouseEntryMapper);
    }

    //获取直播订单
    @Override
    public List<ReturnWarehouseEntry> getLiveOrder(Integer id) {
        return returnWarehouseEntryMapper.getLiveOrder(id);
    }

}
