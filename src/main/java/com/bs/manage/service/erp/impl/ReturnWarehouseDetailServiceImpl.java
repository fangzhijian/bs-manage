package com.bs.manage.service.erp.impl;

import com.bs.manage.mapper.erp.ReturnWarehouseDetailMapper;
import com.bs.manage.model.bean.erp.ReturnWarehouseDetail;
import com.bs.manage.service.common.impl.CommonServiceImpl;
import com.bs.manage.service.erp.ReturnWarehouseDetailService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 2023/4/18 16:02
 * fzj
 */
@Service
public class ReturnWarehouseDetailServiceImpl extends CommonServiceImpl<ReturnWarehouseDetail> implements ReturnWarehouseDetailService {

    private final ReturnWarehouseDetailMapper returnWarehouseDetailMapper;

    public ReturnWarehouseDetailServiceImpl(ReturnWarehouseDetailMapper returnWarehouseDetailMapper) {
        this.returnWarehouseDetailMapper = returnWarehouseDetailMapper;
    }

    @Override
    public void afterPropertiesSet() {
        setCommonMapper(returnWarehouseDetailMapper);
    }

    //获取直播订单
    @Override
    public List<ReturnWarehouseDetail> getLiveOrder(Integer id) {
        return returnWarehouseDetailMapper.getLiveOrder(id);
    }

}
