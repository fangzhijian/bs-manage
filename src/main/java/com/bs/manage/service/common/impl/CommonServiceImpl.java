package com.bs.manage.service.common.impl;

import com.bs.manage.mapper.common.CommonMapper;
import com.bs.manage.model.bean.common.CommonModel;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.service.common.CommonService;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 2020/1/20 16:47
 * fzj
 * 通用服务
 */
@Setter
public abstract class CommonServiceImpl<T extends CommonModel> implements CommonService<T>, InitializingBean {

    private CommonMapper<T> commonMapper;

    @Override
    @Transactional
    public ResponseJson insert(T bean) {
        LocalDateTime now = LocalDateTime.now();
        if (bean.getCreated_at() == null) {
            bean.setCreated_at(now);
        }
        bean.setUpdated_at(now);
        commonMapper.insert(bean);
        return ResponseJson.success();
    }

    @Override
    @Transactional
    public Integer insertBatch(List<T> list) {
        LocalDateTime now = LocalDateTime.now();
        for (T t : list) {
            if (t.getCreated_at() == null) {
                t.setCreated_at(now);
            }
            t.setUpdated_at(now);
        }
        int size = list.size();
        int limit = 500;
        if (size > limit) {
            int n = size % limit == 0 ? size / limit : size / limit + 1;
            int total = 0;
            for (int i = 0; i < n; i++) {
                List<T> ts = new ArrayList<>(limit);
                for (int j = limit * i; j < limit * (i + 1) && j < size; j++) {
                    ts.add(list.get(j));
                }
                total = total + commonMapper.insertBatch(ts);
            }
            return total;
        } else {
            return commonMapper.insertBatch(list);
        }
    }

    @Override
    @Transactional
    public ResponseJson delete(Long id) {
        commonMapper.delete(id, LocalDateTime.now());
        return ResponseJson.success();
    }

    @Override
    @Transactional
    public Integer deleteByIds(List<Long> list) {
        return commonMapper.deleteByIds(list, LocalDateTime.now());
    }

    @Override
    @Transactional
    public ResponseJson update(T bean) {
        bean.setUpdated_at(LocalDateTime.now());
        commonMapper.update(bean);
        return ResponseJson.success();
    }

    @Override
    @Transactional
    public Integer updateBatch(List<T> list) {
        LocalDateTime now = LocalDateTime.now();
        for (T t : list) {
            t.setUpdated_at(now);
        }
        return commonMapper.updateBatch(list);
    }


    @Override
    public T getById(Long id) {
        return commonMapper.getById(id);
    }

    @Override
    public List<T> getAll() {
        return commonMapper.getAll();
    }

    @Override
    public T getOneBySelectKey(T bean) {
        return commonMapper.getOneBySelectKey(bean);
    }

    @Override
    public List<T> getAllBySelectKey(T bean) {
        return commonMapper.getAllBySelectKey(bean);
    }

    @Override
    public abstract void afterPropertiesSet();


}
