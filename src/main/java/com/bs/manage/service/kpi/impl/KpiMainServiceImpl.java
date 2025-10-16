package com.bs.manage.service.kpi.impl;

import com.bs.manage.code.CodeCaption;
import com.bs.manage.code.PubCode;
import com.bs.manage.constant.Constants;
import com.bs.manage.constant.RedisConstants;
import com.bs.manage.intercepter.UserToken;
import com.bs.manage.mapper.kpi.KpiMainMapper;
import com.bs.manage.model.Pair;
import com.bs.manage.model.bean.account.User;
import com.bs.manage.model.bean.common.CommonModel;
import com.bs.manage.model.bean.kpi.KpiContent;
import com.bs.manage.model.bean.kpi.KpiFlowing;
import com.bs.manage.model.bean.kpi.KpiMain;
import com.bs.manage.model.bean.notify.NotifyKpi;
import com.bs.manage.model.json.ExcelData;
import com.bs.manage.model.json.Page;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.model.json.kpi.EmailContent;
import com.bs.manage.model.param.kpi.KpiListParam;
import com.bs.manage.model.param.kpi.KpiListResult;
import com.bs.manage.service.account.UserService;
import com.bs.manage.service.common.DingDingService;
import com.bs.manage.service.common.impl.CommonServiceImpl;
import com.bs.manage.service.configures.ConfiguresService;
import com.bs.manage.service.kpi.KpiContentService;
import com.bs.manage.service.kpi.KpiFlowingService;
import com.bs.manage.service.kpi.KpiMainService;
import com.bs.manage.service.notify.NotifyKpiService;
import com.bs.manage.until.CodeUtil;
import com.bs.manage.until.DateUtil;
import com.bs.manage.until.ExcelUtils;
import com.bs.manage.until.NumberUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 2020/6/15 16:30
 * fzj
 */
@Service
public class KpiMainServiceImpl extends CommonServiceImpl<KpiMain> implements KpiMainService {

    @Value("${webUrl}")
    private String DEFAULT_DING_MSG_URL;
    @Value("${uploadBase}")
    public String uploadBase;

    private final KpiMainMapper kpiMainMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final KpiContentService kpiContentService;
    private final KpiFlowingService kpiFlowingService;
    private final UserService userService;
    private final NotifyKpiService notifyKpiService;
    private final DingDingService dingDingService;
    private final ConfiguresService configuresService;

    public KpiMainServiceImpl(KpiMainMapper kpiMainMapper, RedisTemplate<String, Object> redisTemplate, KpiContentService kpiContentService, KpiFlowingService kpiFlowingService, UserService userService, NotifyKpiService notifyKpiService, DingDingService dingDingService, ConfiguresService configuresService) {
        this.kpiMainMapper = kpiMainMapper;
        this.redisTemplate = redisTemplate;
        this.kpiContentService = kpiContentService;
        this.kpiFlowingService = kpiFlowingService;
        this.userService = userService;
        this.notifyKpiService = notifyKpiService;
        this.dingDingService = dingDingService;
        this.configuresService = configuresService;
    }

    @Override
    public void afterPropertiesSet() {
        setCommonMapper(kpiMainMapper);
    }

    @Override
    public void emailContent(EmailContent emailContent) {
        redisTemplate.opsForValue().set(RedisConstants.KPI_EMAIL, emailContent);
    }

    @Override
    public EmailContent getEmailContent() {
        return (EmailContent) redisTemplate.opsForValue().get(RedisConstants.KPI_EMAIL);
    }

    @Override
    public Page<KpiListResult> getByPage(KpiListParam param) {

        initParam(param);
        User user = UserToken.getContext();

        Page<KpiListResult> page = new Page<>();
        List<KpiListResult> resultList = new ArrayList<>();
        page.setItems(resultList);
        List<Long> parentUserIds = new ArrayList<>();
        List<Long> kpiUserIds = new ArrayList<>();

        //特殊查询 还未制定的考核
        if (param.getStatus() != null && CodeCaption.KPI_STATUS_1 == param.getStatus()) {
            List<KpiMain> kpiMains = this.getAllBySelectKey(KpiMain.builder().month(param.getMonth()).userIds(param.getUserIds()).build());

            if (param.getUserIds() == null) {
                List<User> users;
                if (user.getRoleIds().contains(21) || user.getRoleIds().contains(22)) {
                    users = userService.getAllBySelectKey(User.builder().status(CodeCaption.STATUS_OK).parent_id(-1L).build());
                } else {
                    users = userService.subordinateForAll(user);
                }
                param.setUsers(users);
                param.setUserIds(users.stream().map(CommonModel::getId).collect(Collectors.toList()));
            }
            for (User unUser : param.getUsers()) {
                if (NumberUtil.isNotBlank(unUser.getParent_id())) {
                    boolean hasMake = false;
                    for (KpiMain kpiMain : kpiMains) {
                        if (kpiMain.getUser_id().equals(unUser.getId())) {
                            hasMake = true;
                            break;
                        }
                    }
                    if (!hasMake) {
                        KpiListResult result = KpiListResult.builder().id(0L).user_id(unUser.getId()).username(unUser.getName()).parent_id(unUser.getParent_id()).month(param.getMonth())
                                .status(CodeCaption.KPI_STATUS_1).status_label(CodeUtil.getCaption(CodeCaption.KPI_STATUS, CodeCaption.KPI_STATUS_1)).has_finish(CodeCaption.FALSE).
                                        buttons(new ArrayList<>()).build();
                        parentUserIds.add(unUser.getParent_id());
                        resultList.add(result);
                    }
                }
            }

            int size = resultList.size();
            page.setTotal(size);
            page.setItems(resultList.subList(Math.min(param.getOffset(), size), Math.min(param.getOffset() + param.getLimit(), size)));


        } else {
            Integer count = kpiMainMapper.countByPage(param);
            page.setTotal(count);

            List<KpiMain> kpiMains = kpiMainMapper.getByPage(param);

            for (KpiMain kpiMain : kpiMains) {
                KpiListResult result = new KpiListResult();
                BeanUtils.copyProperties(kpiMain, result);

                int has_finish = CodeCaption.FALSE;
                if (kpiMain.getStatus() == CodeCaption.KPI_STATUS_8 || kpiMain.getStatus() == CodeCaption.KPI_STATUS_9 || kpiMain.getStatus() == CodeCaption.KPI_STATUS_30) {
                    has_finish = CodeCaption.TRUE;
                }
                result.setHas_finish(has_finish);

                result.setStatus_label(CodeUtil.getCaption(CodeCaption.KPI_STATUS, kpiMain.getStatus()));

                //计算评分总和
                Pair<BigDecimal, BigDecimal> sumScore = setSumScore(kpiMain);
                result.setOneself_score(NumberUtil.getAverageBigDecimal(sumScore.getKey(), NumberUtil.HUNDRED, 0));
                result.setSuperior_score(NumberUtil.getAverageBigDecimal(sumScore.getValue(), NumberUtil.HUNDRED, 0));

                //隐藏状态额外信息
                if (CodeCaption.KPI_STATUS_7 != result.getStatus() && CodeCaption.KPI_STATUS_11 != result.getStatus() && CodeCaption.KPI_STATUS_12 != result.getStatus()) {
                    result.setStatus_extra_msg(null);
                }

                List<Integer> buttons = new ArrayList<>();
                boolean oneself = result.getUser_id().equals(user.getId());
                boolean isParent = result.getParent_id().equals(user.getId());
                switch (result.getStatus()) {
                    case CodeCaption.KPI_STATUS_2:
                        if (oneself) {
                            buttons.add(1); //同意
                            buttons.add(2); //不同意(反馈)
                        }
                        if (isParent) {
                            buttons.add(4);//制定考核
                        }
                        break;
                    case CodeCaption.KPI_STATUS_3:
                        if (isParent) {
                            buttons.add(3); //提交到HR,该步骤已取消
                        }
                        break;
                    case CodeCaption.KPI_STATUS_4:
                        if (oneself) {
                            buttons.add(5); //自评
                        }
                        break;
                    case CodeCaption.KPI_STATUS_5:
                        if (oneself) {
                            buttons.add(5); //自评
                        }
                        if (isParent) {
                            buttons.add(6); //上级评分
                        }
                        break;
                    case CodeCaption.KPI_STATUS_6:
                        if (oneself) {
                            buttons.add(7); //确认
                            buttons.add(8); //改进
                        }
                        if (isParent) {
                            buttons.add(6); //上级评分
                        }
                        break;
                    case CodeCaption.KPI_STATUS_7:
                        if (isParent) {
                            buttons.add(9);  //驳回
                            buttons.add(10); //同意改进
                        }
                        break;
                    case CodeCaption.KPI_STATUS_1:
                    case CodeCaption.KPI_STATUS_11:
                    case CodeCaption.KPI_STATUS_12:
                        if (isParent) {
                            buttons.add(4);//制定考核
                        }
                }

                result.setButtons(buttons);
                resultList.add(result);

                kpiUserIds.add(result.getUser_id());
                if (kpiMain.getParent_id() != null) {
                    parentUserIds.add(kpiMain.getParent_id());
                }
            }
        }


        //设置上级姓名
        if (parentUserIds.size() > 0) {
            List<User> parentUsers = userService.getByIds(parentUserIds);
            for (KpiListResult result : resultList) {
                for (User parentUser : parentUsers) {
                    if (parentUser.getId().equals(result.getParent_id())) {
                        result.setParent_name(parentUser.getName());
                        break;
                    }
                }
            }
        }

        //离职人员操作
        boolean hasHrRole = user.getRoleIds().contains(21);
        if (kpiUserIds.size() > 0) {
            List<User> kpiUsers = userService.getByIds(kpiUserIds);
            for (User kpiUser : kpiUsers) {
                if (CodeCaption.STATUS_LEAVE == kpiUser.getStatus()) {
                    for (KpiListResult result : resultList) {
                        if (kpiUser.getId().equals(result.getUser_id())) {
                            if (CodeCaption.KPI_STATUS_30 != result.getStatus() && CodeCaption.KPI_STATUS_8 != result.getStatus() && CodeCaption.KPI_STATUS_9 != result.getStatus()) {
                                //添加离职评分按钮
                                if (hasHrRole || result.getParent_id().equals(user.getId())) {
                                    result.setButtons(Collections.singletonList(30));
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
        return page;
    }

    @Override
    @Transactional
    public ResponseJson updateKpiStatus(Long kpiId, Integer status, String status_extra_msg) {

        KpiMain kpiMain = this.getById(kpiId);
        if (kpiMain == null) {
            return ResponseJson.fail(PubCode.KPI_NOT_MAKE.code(), PubCode.KPI_NOT_MAKE.message());
        }
        User user = UserToken.getContext();
        User kpiUser = userService.getById(kpiMain.getUser_id());
        User parentUser = userService.getById(kpiUser.getParent_id());

        int updateStatus = status;
        String notifyMsg;          //通知消息
        Long notifyUserId = null;  //通知对象id
        String dingMsg;            //钉钉消息
        String dingUserId = null;  //钉钉发送对象id
        String operateUser;        //当前操作人

        //绩效制定后,成员确认绩效
        if (CodeCaption.KPI_STATUS_3 == status) {
            if (CodeCaption.KPI_STATUS_2 != kpiMain.getStatus()) {
                return ResponseJson.fail("绩效状态不是".concat(CodeUtil.getCaption(CodeCaption.KPI_STATUS, CodeCaption.KPI_STATUS_2)).concat(",无法确认绩效方案"));
            }
            updateStatus = CodeCaption.KPI_STATUS_4;
            notifyMsg = String.format("%s已确认%s月的绩效方案，请及时提交至HR", kpiUser.getName(), kpiMain.getMonth());
            notifyUserId = kpiUser.getParent_id();
            dingMsg = String.format("%s已确认%s月的绩效方案，请登录云系统及时提交至HR", kpiUser.getName(), kpiMain.getMonth());
            dingUserId = parentUser.getDd_user_id();
            operateUser = kpiUser.getName();
        }
        //绩效制定后,成员反馈绩效
        else if (CodeCaption.KPI_STATUS_11 == status) {
            if (CodeCaption.KPI_STATUS_2 != kpiMain.getStatus()) {
                return ResponseJson.fail("绩效状态不是".concat(CodeUtil.getCaption(CodeCaption.KPI_STATUS, CodeCaption.KPI_STATUS_2)).concat(",无法反馈"));
            }
            if (!StringUtils.hasText(status_extra_msg)) {
                return ResponseJson.fail("请输入不同意的原因");
            }
            notifyMsg = String.format("%s已对%s月的绩效方案做出反馈，请及时处理", kpiUser.getName(), kpiMain.getMonth());
            notifyUserId = kpiUser.getParent_id();
            dingMsg = String.format("%s已对%s月的绩效方案做出反馈，请登录云系统及时处理", kpiUser.getName(), kpiMain.getMonth());
            dingUserId = parentUser.getDd_user_id();
            operateUser = kpiUser.getName();
        }
        //成员确认绩效后,上级提交绩效计划到HR,已取消该步骤
//        else if (CodeCaption.KPI_STATUS_4 == status) {
//            if (CodeCaption.KPI_STATUS_3 != kpiMain.getStatus()) {
//                return ResponseJson.fail("绩效状态不是".concat(CodeUtil.getCaption(CodeCaption.KPI_STATUS, CodeCaption.KPI_STATUS_3)).concat(",无法提交到HR"));
//            }
//            notifyMsg = String.format("%s已于云系统提交了%s%s月的绩效方案", parentUser.getName(), kpiUser.getName(), kpiMain.getMonth());
//            dingMsg = String.format("%s已提交了%s%s月的绩效方案", parentUser.getName(), kpiUser.getName(), kpiMain.getMonth());
//            operateUser = parentUser.getName();
//        }

        //上级评价之后,成员对评价提出改进
        else if (CodeCaption.KPI_STATUS_7 == status) {
            if (CodeCaption.KPI_STATUS_6 != kpiMain.getStatus()) {
                return ResponseJson.fail("绩效状态不是".concat(CodeUtil.getCaption(CodeCaption.KPI_STATUS, CodeCaption.KPI_STATUS_6)).concat(",无法提出改进"));
            }
            if (!StringUtils.hasText(status_extra_msg)) {
                return ResponseJson.fail("请输入改进原因");
            }
            List<KpiFlowing> kpiFlows = kpiFlowingService.getAllBySelectKey(KpiFlowing.builder().kpi_id(kpiId).build());
            int improveNum = 0;
            for (KpiFlowing kpiFlowing : kpiFlows) {
                if (CodeCaption.KPI_STATUS_12 == kpiFlowing.getStatus()) {
                    improveNum++;
                }
            }
            if (improveNum > 0) {
                return ResponseJson.fail("你已经提出过改进了,不能再次要求改进");
            }
            notifyMsg = kpiUser.getName().concat("提出了").concat(kpiMain.getMonth().toString()).concat("月的绩效方案改进，原因如下：").concat(status_extra_msg).concat("，请及时处理");
            notifyUserId = kpiUser.getParent_id();
            dingMsg = kpiUser.getName().concat("提出了").concat(kpiMain.getMonth().toString()).concat("月的绩效方案改进，原因如下：").concat(status_extra_msg).concat("，请登录云系统及时处理");
            dingUserId = parentUser.getDd_user_id();
            operateUser = kpiUser.getName();
        }
        //上级评价之后,成员对评价进行确认
        else if (CodeCaption.KPI_STATUS_8 == status) {
            if (CodeCaption.KPI_STATUS_6 != kpiMain.getStatus()) {
                return ResponseJson.fail("绩效状态不是".concat(CodeUtil.getCaption(CodeCaption.KPI_STATUS, CodeCaption.KPI_STATUS_6)).concat(",无法确认评价"));
            }
            notifyMsg = kpiUser.getName().concat("上月绩效已确认完毕，可进行工资核算");
            dingMsg = kpiUser.getName().concat("上月绩效已确认完毕，可进行工资核算");
            operateUser = kpiUser.getName();
        }

        //成员对评价提出改进之后,上级驳回
        else if (CodeCaption.KPI_STATUS_9 == status) {
            if (CodeCaption.KPI_STATUS_7 != kpiMain.getStatus()) {
                return ResponseJson.fail("绩效状态不是".concat(CodeUtil.getCaption(CodeCaption.KPI_STATUS, CodeCaption.KPI_STATUS_7)).concat(",无法驳回改进"));
            }

            notifyMsg = kpiUser.getName().concat("上月绩效已确认完毕，可进行工资核算");
            dingMsg = kpiUser.getName().concat("上月绩效已确认完毕，可进行工资核算");
            operateUser = parentUser.getName();
        }
        //成员对评价提出改进之后,上级修改改进
        else if (CodeCaption.KPI_STATUS_12 == status) {
            if (CodeCaption.KPI_STATUS_7 != kpiMain.getStatus()) {
                return ResponseJson.fail("绩效状态不是".concat(CodeUtil.getCaption(CodeCaption.KPI_STATUS, CodeCaption.KPI_STATUS_7)).concat(",无法修改改进"));
            }
            notifyMsg = parentUser.getName().concat("已同意了你的改进");
            notifyUserId = kpiUser.getId();
            dingMsg = parentUser.getName().concat("已同意了你的改进");
            dingUserId = kpiUser.getDd_user_id();
            operateUser = parentUser.getName();
        } else {
            return ResponseJson.fail("无此操作");
        }

        //判断是否是对应操作人
        if (CodeCaption.KPI_STATUS_9 == status || CodeCaption.KPI_STATUS_12 == status) {
            if (!user.getId().equals(kpiUser.getParent_id())) {
                return ResponseJson.fail("你不是该成员的直属上级");
            }
        } else {
            if (!user.getId().equals(kpiUser.getId())) {
                return ResponseJson.fail("你不是当前成员");
            }
        }

        //修改考核主表状态
        this.update(KpiMain.builder().id(kpiId).status(updateStatus).status_extra_msg(status_extra_msg).build());
        //插入考核流程状态
        kpiFlowingService.insert(KpiFlowing.builder().kpi_id(kpiId).status(updateStatus).operate_user(operateUser).build());

        if (CodeCaption.KPI_STATUS_8 == status || CodeCaption.KPI_STATUS_9 == status) {
            long hRId = configuresService.getCodeValue(Constants.CONFIG_KPI_HR);
            if (hRId == 0) {
                return ResponseJson.success();
            }
            User hrUser = userService.getById(hRId);
            notifyUserId = hRId;
            dingUserId = hrUser.getDd_user_id();
        }

        //发送通知
        notifyKpiService.insert(NotifyKpi.builder().kpi_id(kpiMain.getId()).notify_user(notifyUserId).has_read(CodeCaption.FALSE).text(notifyMsg).build());
        dingDingService.notifyForWork(Collections.singletonList(dingUserId), Constants.DEFAULT_DING_TITLE, dingMsg, DEFAULT_DING_MSG_URL, Constants.DEFAULT_DING_PIC_URL);
        return ResponseJson.success();
    }

    @Override
    public ResponseJson getContent(KpiMain kpiMain) {

        if (kpiMain.getUser_id() == null) {
            return ResponseJson.fail("账号id不能为空");
        }
        KpiListParam param = new KpiListParam();
        Integer month = kpiMain.getMonth();

        //作用，前端月份经常传错不是yyyyMM格式，比如传20225自动给补充为202205
        if (month != null && month < 99999) {
            char[] chars = month.toString().toCharArray();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < chars.length; i++) {
                if (i == chars.length - 1) {
                    sb.append("0");
                }
                sb.append(chars[i]);
            }
            kpiMain.setMonth(Integer.parseInt(sb.toString()));
        }
        param.setMonth(kpiMain.getMonth());

        kpiMain.setMonth(initParam(param));
        KpiMain getOne = this.getOneBySelectKey(KpiMain.builder().user_id(kpiMain.getUser_id()).month(kpiMain.getMonth()).build());

        if (getOne == null) {
            return ResponseJson.fail(PubCode.KPI_NOT_MAKE.code(), PubCode.KPI_NOT_MAKE.message());
        }

        List<KpiContent> kpiContents = kpiContentService.getAllBySelectKey(KpiContent.builder().kpi_id(getOne.getId()).build());
        getOne.setContents(kpiContents);
        return ResponseJson.success(getOne);
    }

    @Override
    @Transactional
    public ResponseJson saveContent(KpiMain kpiMain) {

        LocalDate localDate = LocalDate.now();
        User user = UserToken.getContext();
        boolean hasHrRole = user.getRoleIds().contains(21);
        int date = configuresService.getCodeValue(Constants.CONFIG_KPI_DATE);
        if (!hasHrRole && localDate.getDayOfMonth() < 20 && localDate.getDayOfMonth() > date) {
            return ResponseJson.fail("还未到制定考核计划的时间");
        }
        if (kpiMain.getMonth() == null) {
            return ResponseJson.fail("月份不能为空");
        }


        int totalWeight = 0;
        for (KpiContent kpiContent : kpiMain.getContents()) {
            if (kpiContent.getWeight() == null) {
                return ResponseJson.fail("权重值不能为空");
            }
            if (kpiContent.getWeight() < 0 || kpiContent.getWeight() > 100) {
                return ResponseJson.fail("权重值超出范围");
            }
            totalWeight = totalWeight + kpiContent.getWeight();
        }
        if (totalWeight != 100) {
            return ResponseJson.fail("权重总和需要为100%");
        }

        kpiMain.setHas_oneself(CodeCaption.FALSE);
        kpiMain.setStatus(CodeCaption.KPI_STATUS_2);


        User kpiUser = userService.getById(kpiMain.getUser_id());
        if (!hasHrRole && !user.getId().equals(kpiUser.getParent_id())) {
            return ResponseJson.fail("你不是该成员的直属上级");
        }

        //保存绩效考核主表
        KpiMain getOne = this.getOneBySelectKey(KpiMain.builder().user_id(kpiMain.getUser_id()).month(kpiMain.getMonth()).build());

        if (getOne != null) {
            if (getOne.getStatus() != CodeCaption.KPI_STATUS_1 && getOne.getStatus() != CodeCaption.KPI_STATUS_2
                    && getOne.getStatus() != CodeCaption.KPI_STATUS_11 && getOne.getStatus() != CodeCaption.KPI_STATUS_12) {
                return ResponseJson.fail("绩效已制定并且确认,无法修改");
            }
            kpiMain.setId(getOne.getId());
            kpiMain.setCreated_at(getOne.getCreated_at());
            this.update(kpiMain);
        } else {
            this.insert(kpiMain);
        }

        for (KpiContent kpiContent : kpiMain.getContents()) {
            kpiContent.setKpi_id(kpiMain.getId());
        }

        //插入考核详情
        kpiContentService.deleteByKpiId(kpiMain.getId());
        kpiContentService.insertBatch(kpiMain.getContents());

        //插入考核流程状态
        kpiFlowingService.insert(KpiFlowing.builder().kpi_id(kpiMain.getId()).status(CodeCaption.KPI_STATUS_2).operate_user(user.getName()).build());

        //发送通知
        notifyKpiService.insert(NotifyKpi.builder().kpi_id(kpiMain.getId()).notify_user(kpiMain.getUser_id()).has_read(CodeCaption.FALSE).text(String.format("请于今日确认%s月的绩效方案", kpiMain.getMonth())).build());
        dingDingService.notifyForWork(Collections.singletonList(kpiUser.getDd_user_id()), Constants.DEFAULT_DING_TITLE, String.format("请于今日登录云系统确认%s月的绩效方案", kpiMain.getMonth()), DEFAULT_DING_MSG_URL, Constants.DEFAULT_DING_PIC_URL);
        return ResponseJson.success(kpiMain);
    }

    /**
     * 查询流水状态
     *
     * @param kpi_id kpi主表id
     * @return 流水状态信息
     */
    @Override
    public ResponseJson flowProgress(Long kpi_id) {
        List<KpiFlowing> flowingList = kpiFlowingService.getAllBySelectKey(KpiFlowing.builder().kpi_id(kpi_id).build());
        KpiMain kpiMain = this.getById(kpi_id);
        if (kpiMain == null || flowingList.size() == 0) {
            return ResponseJson.fail("考核信息不存在");
        }
        flowingList.sort(Comparator.comparing(CommonModel::getCreated_at));


        //已完成的流程状态
        for (KpiFlowing flowing : flowingList) {
            flowing.setStatus_label(CodeUtil.getCaption(CodeCaption.KPI_STATUS, flowing.getStatus()));
            flowing.setFinish_status(1);  //该状态已完成
        }


        //当前流程状态
        KpiFlowing presentFlowing = flowingList.get(flowingList.size() - 1);
        presentFlowing.setFinish_status(2);             //该状态进行中
        int presentStatus = presentFlowing.getStatus(); //当前状态

        //离职人员直接一步到位
        User kpiUser = userService.getById(kpiMain.getUser_id());
        if (CodeCaption.STATUS_LEAVE == kpiUser.getStatus()
                && CodeCaption.KPI_STATUS_30 != presentStatus && CodeCaption.KPI_STATUS_8 != presentStatus && CodeCaption.KPI_STATUS_9 != presentStatus) {
            flowingList.add(KpiFlowing.builder().status(CodeCaption.KPI_STATUS_8).status_label("离职评分").operate_user("上级或HR权限员").finish_status(3).build());
            return ResponseJson.success(flowingList);
        }


        //设置待进行的流程状态

        User parentUser = userService.getById(kpiUser.getParent_id());
        List<KpiFlowing> futureFlowingList = new ArrayList<>();   //通用的流程顺序
        futureFlowingList.add(KpiFlowing.builder().status(CodeCaption.KPI_STATUS_2).status_label("制定绩效方案").operate_user(parentUser.getName()).finish_status(3).build());
        futureFlowingList.add(KpiFlowing.builder().status(CodeCaption.KPI_STATUS_3).status_label("确认绩效方案").operate_user(kpiUser.getName()).finish_status(3).build());
        futureFlowingList.add(KpiFlowing.builder().status(CodeCaption.KPI_STATUS_5).status_label("自评").operate_user(kpiUser.getName()).finish_status(3).build());
        futureFlowingList.add(KpiFlowing.builder().status(CodeCaption.KPI_STATUS_6).status_label("上级评价").operate_user(parentUser.getName()).finish_status(3).build());
        futureFlowingList.add(KpiFlowing.builder().status(CodeCaption.KPI_STATUS_8).status_label("确认绩效评价").operate_user(kpiUser.getName()).finish_status(3).build());

        if (CodeCaption.KPI_STATUS_7 == presentStatus) {
            //成员提出改进默认下个流程主管修改改进中
            futureFlowingList.add(KpiFlowing.builder().status(CodeCaption.KPI_STATUS_12).status_label("确认绩效改进").operate_user(parentUser.getName()).finish_status(3).build());
            flowingList.addAll(futureFlowingList);
        } else {
            for (KpiFlowing futureFlowing : futureFlowingList) {
                if (CodeCaption.KPI_STATUS_11 == presentStatus || CodeCaption.KPI_STATUS_12 == presentStatus || futureFlowing.getStatus() > presentStatus) {
                    flowingList.add(futureFlowing);
                }
            }
        }

        return ResponseJson.success(flowingList);
    }

    @Override
    @Transactional
    public ResponseJson updateContent(KpiMain kpiMain) {
        KpiMain getOne = this.getById(kpiMain.getId());
        if (getOne == null) {
            return ResponseJson.fail(PubCode.KPI_NOT_MAKE.code(), PubCode.KPI_NOT_MAKE.message());
        }
        User user = UserToken.getContext();
        User kpiUser = userService.getById(getOne.getUser_id());
        int status;

        //离职评分
        if (CodeCaption.STATUS_LEAVE == kpiUser.getStatus()) {
            if (!user.getRoleIds().contains(21) && !user.getId().equals(kpiUser.getParent_id())) {
                return ResponseJson.fail("该员工已离职,需要上级或者HR权限员才能评分");
            }
            if (CodeCaption.KPI_STATUS_30 == getOne.getStatus() && CodeCaption.KPI_STATUS_8 == getOne.getStatus() && CodeCaption.KPI_STATUS_9 == getOne.getStatus()) {
                return ResponseJson.fail("已考核完毕无需再评分");
            }
            status = CodeCaption.KPI_STATUS_30;

        } else {
            //自评
            if (user.getId().equals(kpiUser.getId())) {
                if (getOne.getStatus() != CodeCaption.KPI_STATUS_4 && getOne.getStatus() != CodeCaption.KPI_STATUS_5) {
                    return ResponseJson.fail("绩效状态不是".concat(CodeUtil.getCaption(CodeCaption.KPI_STATUS, CodeCaption.KPI_STATUS_4)).concat(",无法自评"));
                }
                if (!user.getId().equals(kpiUser.getId())) {
                    return ResponseJson.fail("只有自己才能自评");
                }
                for (KpiContent kpiContent : kpiMain.getContents()) {
                    kpiContent.setSuperior_score(null);
                    if (kpiContent.getOneself_score() == null) {
                        return ResponseJson.fail("评分不能为空");
                    }
                    //服了 这评分还能为两位小数和大于100分
                    if (kpiContent.getOneself_score().compareTo(BigDecimal.ZERO) < 0) {
                        return ResponseJson.fail("评分范围不低于0");
                    }
                }
                status = CodeCaption.KPI_STATUS_5;
                getOne.setHas_oneself(CodeCaption.TRUE);
                //上级评价
            } else if (user.getId().equals(kpiUser.getParent_id())) {
                if (getOne.getStatus() != CodeCaption.KPI_STATUS_5 && getOne.getStatus() != CodeCaption.KPI_STATUS_6) {
                    return ResponseJson.fail("绩效状态不是".concat(CodeUtil.getCaption(CodeCaption.KPI_STATUS, CodeCaption.KPI_STATUS_5)).concat(",上级无法评价"));
                }
                if (!kpiUser.getParent_id().equals(user.getId())) {
                    return ResponseJson.fail("你不是该成员的直属上级");
                }
                for (KpiContent kpiContent : kpiMain.getContents()) {
                    kpiContent.setOneself_score(null);
                    if (kpiContent.getSuperior_score() == null) {
                        return ResponseJson.fail("评分不能为空");
                    }
                    if (kpiContent.getSuperior_score().compareTo(BigDecimal.ZERO) < 0) {
                        return ResponseJson.fail("评分范围不低于0");
                    }
                }
                status = CodeCaption.KPI_STATUS_6;
            } else {
                return ResponseJson.fail("他人无法评分");
            }
        }

        //修改考核主表状态
        getOne.setStatus(status);
        this.update(getOne);

        //修改考核详情中的评分
        kpiContentService.updateBatch(kpiMain.getContents());

        //插入考核流程状态
        kpiFlowingService.insert(KpiFlowing.builder().kpi_id(kpiMain.getId()).status(status).operate_user(user.getName()).build());


        //上级评分时发送通知
        if (status == CodeCaption.KPI_STATUS_6) {
            notifyKpiService.insert(NotifyKpi.builder().kpi_id(kpiMain.getId()).notify_user(kpiMain.getUser_id()).has_read(CodeCaption.FALSE).text("您的上月绩效已考核完毕，请于今日登录云系统进行确认").build());
            dingDingService.notifyForWork(Collections.singletonList(kpiUser.getDd_user_id()), Constants.DEFAULT_DING_TITLE, "您的上月绩效已考核完毕，请于今日确认", DEFAULT_DING_MSG_URL, Constants.DEFAULT_DING_PIC_URL);
        } else {
            User parentUser = userService.getById(kpiUser.getParent_id());
            notifyKpiService.insert(NotifyKpi.builder().kpi_id(kpiMain.getId()).notify_user(user.getParent_id()).has_read(CodeCaption.FALSE).text(user.getName().concat("已自评完毕，请及时完成上级评价")).build());
            dingDingService.notifyForWork(Collections.singletonList(parentUser.getDd_user_id()), Constants.DEFAULT_DING_TITLE, user.getName().concat("已自评完毕，请及时完成上级评价"), DEFAULT_DING_MSG_URL, Constants.DEFAULT_DING_PIC_URL);
        }

        getOne.setContents(kpiContentService.getAllBySelectKey(KpiContent.builder().kpi_id(kpiMain.getId()).build()));
        return ResponseJson.success(getOne);
    }

    /**
     * 获取下属成员
     *
     * @param type 1-可读成员 2- 可编写成员
     * @return 账号列表
     */
    @Override
    public List<User> getSubordinate(Integer type) {
        User user = UserToken.getContext();
        if (user.getRoleIds().contains(21) || (type == 1 && user.getRoleIds().contains(22))) {
            return userService.getAllBySelectKey(User.builder().status(CodeCaption.STATUS_OK).build());
        }
        List<User> users;
        if (type == 1) {
            users = userService.subordinateForAll(user);
            users.add(0, user);
        } else {
            users = userService.getAllBySelectKey(User.builder().status(CodeCaption.STATUS_OK).parent_id(user.getId()).build());
        }
        return users;
    }

    @Override
    public void exportList(KpiListParam param, HttpServletResponse response) {
        param.setOffset(0);
        param.setLimit(1000);  //一个月的数据量大概100不到
        Page<KpiListResult> page = this.getByPage(param);
        ExcelData excelData = new ExcelData();
        String[] columnNames = {"序号", "账号id", "姓名", "年月份", "状态", "上级姓名", "已考核", "自评得分", "上级评分", "制定时间"};// 列名
        excelData.setColumnNames(columnNames);
        List<List<Object>> rows = new ArrayList<>(page.getTotal());
        for (int i = 0; i < page.getItems().size(); i++) {
            List<Object> row = new ArrayList<>();
            KpiListResult result = page.getItems().get(i);
            row.add(i + 1);
            row.add(result.getUser_id());
            row.add(result.getUsername());
            row.add(result.getMonth());
            row.add(result.getStatus_label());
            row.add(result.getParent_name());
            row.add(CodeUtil.getCaption(CodeCaption.BOOLEAN, result.getHas_finish()));
            row.add(result.getOneself_score());
            row.add(result.getSuperior_score());
            row.add(DateUtil.DATE_TIME_FORMATTER.format(result.getCreated_at()));
            rows.add(row);
        }
        excelData.setRows(rows);
        String excelName = "考核";
        if (param.getMonth() != null) {
            excelName = param.getMonth().toString().concat("月考核");
        }
        ExcelUtils.exportExcel(response, excelName, excelData);
    }

    @Override
    public void exportDetail(KpiListParam param, HttpServletResponse response) {
        initParam(param);
        param.setOffset(0);
        param.setLimit(1000);
        List<KpiMain> kpiMains = kpiMainMapper.getByPage(param);
        if (kpiMains.size() == 0) {
            return;
        }

        List<ExcelData> excelDataList = new ArrayList<>();
        for (KpiMain kpiMain : kpiMains) {
            //计算评分总和
            Pair<BigDecimal, BigDecimal> sumScore = setSumScore(kpiMain);
            ExcelData excelData = new ExcelData();
            String[] columnNames = {"序号", "考核维度", "kpi指标", "指标说明", "衡量标准", "自评达成", "实际达成", "权重百分比", "自评得分", "上级评分"};// 列名
            excelData.setColumnNames(columnNames);
            List<List<Object>> rows = new ArrayList<>();
            for (int i = 0; i < kpiMain.getContents().size(); i++) {
                List<Object> row = new ArrayList<>();
                KpiContent kpiContent = kpiMain.getContents().get(i);
                row.add(i + 1);
                row.add(kpiContent.getKpi_type());
                row.add(kpiContent.getKpi_label());
                row.add(kpiContent.getKpi_content());
                row.add(kpiContent.getKpi_goal());
                row.add(kpiContent.getOneself_deal());
                row.add(kpiContent.getActual_deal());
                row.add(kpiContent.getWeight().toString().concat("%"));
                row.add(kpiContent.getOneself_score());
                row.add(kpiContent.getSuperior_score());
                rows.add(row);
            }
            //最后一行放入合计值
            List<Object> row = new ArrayList<>();
            row.add("合计");
            row.add(null);
            row.add(null);
            row.add(null);
            row.add(null);
            row.add(null);
            row.add(null);
            row.add(null);
            row.add(NumberUtil.getAverageBigDecimal(sumScore.getKey(), NumberUtil.HUNDRED, 0));
            row.add(NumberUtil.getAverageBigDecimal(sumScore.getValue(), NumberUtil.HUNDRED, 0));
            rows.add(row);
            excelData.setRows(rows);

            String username = userService.getById(kpiMain.getUser_id()).getName();
            excelData.setExcelName(username.concat(kpiMain.getMonth().toString()).concat("月个人考核"));
            excelDataList.add(excelData);
        }

        String excelName = "考核明细";
        if (param.getMonth() != null) {
            excelName = param.getMonth().toString().concat("月考核明细");
        }
        ExcelUtils.exportZip(response, excelName, excelDataList, uploadBase);
    }

    /**
     * 初始化参数
     */
    private Integer initParam(KpiListParam param) {
        //勾选了id则无需其他参数，直接结速返回
        if (StringUtils.hasText(param.getIds())) {
            param.setMonth(null);
            param.setStatus(null);
            param.setHas_finish(null);
            param.setUsername(null);
            param.setUsername(null);
            return 0;
        }

        User user = UserToken.getContext();
        boolean hasHrRole = user.getRoleIds().contains(22) || user.getRoleIds().contains(21);
        boolean onlyLookOne = StringUtils.hasText(param.getUsername());
        //HR可以看所有成员,不用设置userId列表
        if (!hasHrRole && !onlyLookOne) {
            //这里要查看包含已离职人员的绩效
            List<User> users = userService.getAll();
            List<User> sonUsers = new ArrayList<>();
            getSonUsers(users, sonUsers, user.getId());
            sonUsers.add(user);
            param.setUsers(sonUsers);
            param.setUserIds(sonUsers.stream().map(CommonModel::getId).collect(Collectors.toList()));
        }

        //设置查看月份
        Integer queryMonth = param.getMonth();
        if (queryMonth == null) {
            LocalDate localDate = LocalDate.now();
            if (localDate.getDayOfMonth() >= 20) {
                queryMonth = Integer.parseInt(localDate.format(DateUtil.yyyyMM_FORMATTER));
            } else {
                queryMonth = Integer.parseInt(localDate.minusMonths(1).format(DateUtil.yyyyMM_FORMATTER));
            }
        }
        param.setMonth(queryMonth);
        return queryMonth;
    }

    private void getSonUsers(List<User> users, List<User> sonUsers, Long parentId) {
        for (User user : users) {
            if (parentId.equals(user.getParent_id())) {
                sonUsers.add(user);
                getSonUsers(users, sonUsers, user.getId());
            }
        }
    }

    /**
     * 计算评分总和
     *
     * @param kpiMain 考核信息
     */
    private Pair<BigDecimal, BigDecimal> setSumScore(KpiMain kpiMain) {
        BigDecimal oneself_score = BigDecimal.ZERO;      //自评得分
        BigDecimal superior_score = BigDecimal.ZERO;     //上级评分
        for (KpiContent kpiContent : kpiMain.getContents()) {
            if (kpiContent.getOneself_score() != null) {
                oneself_score = oneself_score.add(kpiContent.getOneself_score().multiply(new BigDecimal(kpiContent.getWeight())));
            }
            if (kpiContent.getSuperior_score() != null) {
                superior_score = superior_score.add(kpiContent.getSuperior_score().multiply(new BigDecimal(kpiContent.getWeight())));
            }
        }
        return new Pair<>(oneself_score, superior_score);
    }
}
