package com.bs.manage.service.region.impl;

import com.bs.manage.code.CodeCaption;
import com.bs.manage.mapper.region.RegionMapper;
import com.bs.manage.model.bean.region.Region;
import com.bs.manage.model.json.MultiLabel;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.service.common.impl.CommonServiceImpl;
import com.bs.manage.service.region.RegionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 2020/3/2 15:17
 * fzj
 */
@Service
public class RegionServiceImpl extends CommonServiceImpl<Region> implements RegionService {


    private final RegionMapper regionMapper;

    public RegionServiceImpl(RegionMapper regionMapper) {
        this.regionMapper = regionMapper;
    }

    @Override
    public void afterPropertiesSet() {
        setCommonMapper(regionMapper);
    }


    @Override
    public ResponseJson getAllRegion() {
        List<Region> regions = super.getAll();
        List<MultiLabel> topLabelList = new ArrayList<>();
        Iterator<Region> iterator = regions.iterator();
        while (iterator.hasNext()) {
            Region next = iterator.next();
            if (next.getType() == CodeCaption.REGION_PROVINCE) {
                topLabelList.add(new MultiLabel().setId(next.getId()).setValue(next.getId()).setLabel(next.getName()));
                iterator.remove();
            }
        }
        MultiLabel.setChild(topLabelList,regions);
        return ResponseJson.success(topLabelList);
    }
}
