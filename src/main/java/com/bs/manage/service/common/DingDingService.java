package com.bs.manage.service.common;

import com.bs.manage.model.json.ResponseJson;

import java.util.List;

/**
 * 2020/6/10 16:49
 * fzj
 */
public interface DingDingService {

    /**
     * 获取所在企业的钉钉userId
     *
     * @param phone 手机号
     * @return 钉钉userId
     */
    ResponseJson getUserId(String phone);

    /**
     * 发送钉钉工作通知
     *
     * @param userIdList 钉钉userId列表
     * @param title      标题
     * @param text       内容
     * @param messageUrl 消息跳转链接
     * @param picUrl     消息附带的图片链接
     */
    void notifyForWork(List<String> userIdList, String title, String text, String messageUrl, String picUrl);
}
