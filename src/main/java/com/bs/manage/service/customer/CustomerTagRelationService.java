package com.bs.manage.service.customer;

import com.bs.manage.model.bean.customer.CustomerTagRelation;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.model.param.customer.BatchTagParam;
import com.bs.manage.service.common.CommonService;

import java.util.List;

/**
 * 2020/3/2 17:17
 * fzj
 */
public interface CustomerTagRelationService extends CommonService<CustomerTagRelation> {

    /**
     * 批量标签管理(批量打标)
     * 多个用户打相同的多个标签
     * <p>
     * ids  客户id数组
     * tags 标签id数组
     *
     * @return 是否打标签成功
     */
    ResponseJson batchAttachTags(BatchTagParam param);

    /**
     * 单个标签管理
     * 单个用户分离标签(即删除一个标签)
     *
     * @param customer_id 客户id
     * @param tag_id      标签id
     * @return 是否分离标签成功
     */
    ResponseJson detachTag(Long customer_id, Long tag_id);

    /**
     * 根据客户id匹配标签列表
     *
     * @param list 客户id
     * @return 标签列表
     */
    List<CustomerTagRelation> getCustomerTags(List<Long> list);


    /**
     * 判断标签是否被使用
     *
     * @param tagId 标签id
     * @return true or false
     */
    Boolean existForTag(Long tagId);

}
