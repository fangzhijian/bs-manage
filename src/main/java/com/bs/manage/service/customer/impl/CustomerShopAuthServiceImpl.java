package com.bs.manage.service.customer.impl;

import com.bs.manage.code.CodeCaption;
import com.bs.manage.constant.Constants;
import com.bs.manage.mapper.customer.CustomerShopAuthMapper;
import com.bs.manage.model.bean.customer.CustomerShopAuth;
import com.bs.manage.model.bean.notify.NotifyCustomerAuth;
import com.bs.manage.model.bean.upload.UploadData;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.service.common.impl.CommonServiceImpl;
import com.bs.manage.service.configures.ConfiguresService;
import com.bs.manage.service.customer.CustomerShopAuthService;
import com.bs.manage.service.notify.NotifyCustomerAuthService;
import com.bs.manage.service.upload.UploadDataService;
import com.bs.manage.until.DateUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 2020/4/13 9:56
 * fzj
 */
@Service
public class CustomerShopAuthServiceImpl extends CommonServiceImpl<CustomerShopAuth> implements CustomerShopAuthService {

    private final CustomerShopAuthMapper customerShopAuthMapper;
    private final NotifyCustomerAuthService notifyCustomerAuthService;
    private final ConfiguresService configuresService;
    private final UploadDataService uploadDataService;

    public CustomerShopAuthServiceImpl(CustomerShopAuthMapper customerShopAuthMapper, NotifyCustomerAuthService notifyCustomerAuthService, ConfiguresService configuresService, UploadDataService uploadDataService) {
        this.customerShopAuthMapper = customerShopAuthMapper;
        this.notifyCustomerAuthService = notifyCustomerAuthService;
        this.configuresService = configuresService;
        this.uploadDataService = uploadDataService;
    }

    @Override
    public void afterPropertiesSet() {
        setCommonMapper(customerShopAuthMapper);
    }


    @Override
    @Transactional
    public ResponseJson insert(CustomerShopAuth bean) {
        UploadData uploadData = uploadDataService.getById(bean.getData_id());
        if (uploadData == null) {
            return ResponseJson.fail("无效的证书链接,请重新上传");
        }
        bean.setData_name(uploadData.getOriginal_name());
        bean.setLink(uploadData.getUrl());
        uploadDataService.markUse(CodeCaption.TRUE, bean.getData_id(), bean.getLink(), null);
        return super.insert(bean);
    }

    @Override
    @Transactional
    public ResponseJson delete(Long id) {
        CustomerShopAuth customerAuth = customerShopAuthMapper.getById(id);
        if (customerAuth == null) {
            return ResponseJson.fail("授权信息已经被删除了");
        }
        uploadDataService.markUse(CodeCaption.FALSE, customerAuth.getData_id(), customerAuth.getLink(), null);
        return super.delete(id);
    }

    @Override
    @Transactional
    public ResponseJson update(CustomerShopAuth bean) {
        CustomerShopAuth customerShopAuth = customerShopAuthMapper.getById(bean.getId());
        if (customerShopAuth == null) {
            return ResponseJson.fail("授权信息已不存在");
        }
        if (!customerShopAuth.getData_id().equals(bean.getData_id())) {
            UploadData uploadData = uploadDataService.getById(bean.getData_id());
            if (uploadData == null) {
                return ResponseJson.fail("无效的证书链接,请重新上传");
            }
            bean.setData_name(uploadData.getOriginal_name());
            bean.setLink(uploadData.getUrl());
            uploadDataService.markUse(CodeCaption.FALSE, customerShopAuth.getData_id(), customerShopAuth.getLink(), null);
            uploadDataService.markUse(CodeCaption.TRUE, bean.getData_id(), bean.getLink(), null);
        }
        return super.update(bean);
    }

    /**
     * 新增客户授权到期提醒
     */
    @Transactional
    public void insertToNotifyCustomerAuth() {
        String notifyDate = LocalDateTime.now().plusDays(14).format(DateUtil.DATE_FORMATTER);
        int userId = configuresService.getCodeValue(Constants.CONFIG_NOTIFY_USER);
        List<NotifyCustomerAuth> nearExpireAuth = customerShopAuthMapper.getNearExpireAuth(notifyDate, userId);
        if (nearExpireAuth.size() > 0) {
            notifyCustomerAuthService.insertBatch(nearExpireAuth);
        }

    }
}
