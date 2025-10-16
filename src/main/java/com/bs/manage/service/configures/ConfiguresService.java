package com.bs.manage.service.configures;

import com.bs.manage.model.bean.configures.Configures;
import com.bs.manage.service.common.CommonService;

/**
 * 2020/3/12 10:04
 * fzj
 */
public interface ConfiguresService extends CommonService<Configures> {

    /**
     * 查询code对应的值
     * @param code code
     * @return 值
     */
    int getCodeValue(Integer code);

}
