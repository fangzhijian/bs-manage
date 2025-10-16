package com.bs.manage.service.common.impl;

import com.bs.manage.code.PubCode;
import com.bs.manage.constant.TokenConstants;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.service.common.DingDingService;
import com.bs.manage.service.token.TokenService;
import com.bs.manage.until.DateUtil;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.request.OapiUserGetByMobileRequest;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.dingtalk.api.response.OapiUserGetByMobileResponse;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 2020/6/10 16:49
 * fzj
 */
@Service
@Slf4j
public class DingDingServiceImpl implements DingDingService {

    @Value("${agentIdDingDing}")
    private long agentIdDingDing;

    private final TokenService tokenService;

    public DingDingServiceImpl(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    /**
     * 获取所在企业的钉钉userId
     *
     * @param phone 手机号
     * @return 钉钉userId
     */
    @Override
    public ResponseJson getUserId(String phone) {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/get_by_mobile");
        OapiUserGetByMobileRequest request = new OapiUserGetByMobileRequest();
        request.setMobile(phone);
        try {
            OapiUserGetByMobileResponse response = client.execute(request, tokenService.getToken(TokenConstants.DING_DING));
            if (response.getErrcode() != PubCode.SUCCESS_CODE) {
                return ResponseJson.fail(response.getErrmsg());
            }
            return ResponseJson.success(response.getUserid());
        } catch (ApiException e) {
            log.error(e.getErrMsg(), e);
            return ResponseJson.fail("钉钉调用异常");
        }
    }

    /**
     * 发送钉钉工作通知
     *
     * @param userIdList 钉钉userId列表
     * @param title      标题
     * @param text       内容
     * @param messageUrl 消息跳转链接
     * @param picUrl     消息附带的图片链接
     */
    @Override
    public void notifyForWork(List<String> userIdList, String title, String text, String messageUrl, String picUrl) {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2");

        OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
        request.setUseridList(String.join(",", userIdList));
        request.setAgentId(agentIdDingDing);
        request.setToAllUser(false);
        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();

        msg.setMsgtype("link");
        msg.setLink(new OapiMessageCorpconversationAsyncsendV2Request.Link());
        msg.getLink().setTitle(title);
        //相同消息同一个人一天只接收一次 ,这里默认加时间戳
        msg.getLink().setText(text.concat("[").concat(LocalDateTime.now().format(DateUtil.DATE_TIME_FORMATTER)).concat("]"));
        msg.getLink().setMessageUrl(messageUrl);
        msg.getLink().setPicUrl(picUrl);
        request.setMsg(msg);

        try {
            OapiMessageCorpconversationAsyncsendV2Response response = client.execute(request, tokenService.getToken(TokenConstants.DING_DING));
            if (response.getErrcode() != PubCode.SUCCESS_CODE) {
                log.error(response.getErrmsg());
            } else {
                log.info("工作通知消息发送成功,任务id{}", response.getTaskId());
            }
        } catch (ApiException e) {
            log.error(e.getErrMsg(), e);
        }
    }
}
