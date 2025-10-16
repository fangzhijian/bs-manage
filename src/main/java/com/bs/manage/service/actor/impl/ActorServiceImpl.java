package com.bs.manage.service.actor.impl;

import com.bs.manage.code.CodeCaption;
import com.bs.manage.code.PubCode;
import com.bs.manage.intercepter.UserToken;
import com.bs.manage.mapper.actor.ActorMapper;
import com.bs.manage.model.bean.account.User;
import com.bs.manage.model.bean.actor.Actor;
import com.bs.manage.model.json.ExcelData;
import com.bs.manage.model.json.Page;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.model.param.actor.ActorSearchParam;
import com.bs.manage.service.actor.ActorService;
import com.bs.manage.service.common.impl.CommonServiceImpl;
import com.bs.manage.until.DateUtil;
import com.bs.manage.until.ExcelUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


/**
 * 2020/3/5 15:16
 * fzj
 */
@Service
@Slf4j
public class ActorServiceImpl extends CommonServiceImpl<Actor> implements ActorService {

    private final ActorMapper actorMapper;

    public ActorServiceImpl(ActorMapper actorMapper) {
        this.actorMapper = actorMapper;
    }


    @Override
    @Transactional(propagation = Propagation.NESTED)
    public ResponseJson insert(Actor bean) {
        if (super.getOneBySelectKey(Actor.builder().platform_id(bean.getPlatform_id()).build()) != null) {
            return ResponseJson.fail("平台id重复");
        }
        String maxSn = actorMapper.getMaxSn();
        String sn = getNextSn(maxSn);
        bean.setSn(sn);
        User user = UserToken.getContext();
        bean.setCreate_user_id(user.getId());
        bean.setCreate_user_name(user.getName());
        super.insert(bean);
        return ResponseJson.success(bean);
    }

    @Override
    @Transactional
    public ResponseJson delete(Long id) {
        Actor actor = super.getById(id);
        if (actor == null) {
            return ResponseJson.fail("达人已删除");
        }
        return super.delete(id);
    }

    @Transactional
    @Override
    public ResponseJson update(Actor bean) {
        Actor actor = super.getById(bean.getId());
        if (actor == null) {
            return ResponseJson.fail("该达人不存在");
        }
        bean.setSn(null);
        bean.setPlatform_id(null);
        super.update(bean);
        bean.setSn(actor.getSn());
        bean.setPlatform_id(actor.getPlatform_id());
        return ResponseJson.success(bean);
    }

    @Override
    public ResponseJson getByPage(ActorSearchParam param) {
        initParam(param);
        Page<Actor> page = new Page<>();
        Integer count = actorMapper.countByPage(param);
        page.setTotal(count);
        if (count > 0) {
            List<Actor> actors = actorMapper.getByPage(param);
            page.setItems(actors);
        }
        return ResponseJson.success(page);
    }

    @Override
    public ResponseJson processAll(Long id) {
        Actor actor = super.getById(id);
        return ResponseJson.success(actor);
    }

    @Override
    public ResponseJson importActor(MultipartFile file) {
        Sheet sheet = ExcelUtils.getSheet(file);
        int totalRow = sheet.getLastRowNum();
        List<Actor> actorList = new ArrayList<>();
        List<Actor> failedList = new ArrayList<>();

        for (int i = 1; i < totalRow + 1; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            Actor actor = new Actor();
            actor.setActor_name(ExcelUtils.getString(row.getCell(0)));
            actor.setPlatform_id(ExcelUtils.getString(row.getCell(1)));
            actor.setWx_id(ExcelUtils.getString(row.getCell(2)));
            actor.setLabel(ExcelUtils.getString(row.getCell(3)));
            actor.setHomepage_link(ExcelUtils.getString(row.getCell(4)));
            actor.setOrganization(ExcelUtils.getString(row.getCell(5)));
            actor.setAttribute_one(ExcelUtils.getString(row.getCell(6)));
            actor.setAttribute_two(ExcelUtils.getString(row.getCell(7)));
            actor.setAttribute_extension(ExcelUtils.getString(row.getCell(8)));
            actor.setQuoted_price(ExcelUtils.getBigDecimal(row.getCell(9)));
            actor.setFans_num(ExcelUtils.getInteger(row.getCell(10)));
            actor.setProvince(ExcelUtils.getString(row.getCell(11)));
            actor.setCity(ExcelUtils.getString(row.getCell(12)));
            actor.setShipping_address(ExcelUtils.getString(row.getCell(13)));
            actor.setLinkman(ExcelUtils.getString(row.getCell(14)));
            actor.setContact_number(ExcelUtils.getString(row.getCell(15)));
            actor.setJob_wx(ExcelUtils.getString(row.getCell(16)));
            actor.setBusiness_owner(ExcelUtils.getString(row.getCell(17)));
            actor.setTotal_view(ExcelUtils.getString(row.getCell(18)));
            actor.setAverage_view(ExcelUtils.getString(row.getCell(19)));
            actor.setRemark(ExcelUtils.getString(row.getCell(20)));
            actorList.add(actor);
        }
        for (Actor actor : actorList) {
            try {
                ResponseJson responseJson = insert(actor);
                if (PubCode.SUCCESS.code() != responseJson.getCode()) {
                    log.error(responseJson.getMsg());
                    actor.setInsertError(responseJson.getMsg());
                    failedList.add(actor);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                actor.setInsertError("网络开小差");
                failedList.add(actor);
            }
        }
        if (failedList.size() > 0) {
            return ResponseJson.fail(PubCode.BUSINESS_ERROR.code(), String.format("存在%s条数据导入失败", failedList.size()), failedList);
        } else {
            return ResponseJson.success();
        }
    }

    @Override
    public void exportList(ActorSearchParam param, HttpServletResponse response) {
        initParam(param);
        Integer count = actorMapper.countByPage(param);
        List<Actor> actorList = new ArrayList<>();
        if (count > 0) {
            int limit = 1000;
            int order = count / limit + 1;
            for (int i = 0; i < order; i++) {
                param.setLimit(limit);
                param.setOffset(limit * i);
                actorList.addAll(actorMapper.getByPage(param));
            }
        }

        ExcelData excelData = new ExcelData();
        String[] columnNames = {"序号", "达人编号", "达人名称", "平台id", "微信号", "标签", "主页链接", "所属机构", "一级属性", "二级属性", "推广属性",
                "合作报价", "粉丝数", "所在省份", "所在城市", "收货地址", "联系人", "联系电话", "对接微信", "业务负责人","总播放量","平均观场","备注","录入人","录入时间"};// 列名
        excelData.setColumnNames(columnNames);
        List<List<Object>> rows = new ArrayList<>(actorList.size());
        for (int i = 0; i < actorList.size(); i++) {
            List<Object> row = new ArrayList<>();
            Actor actor = actorList.get(i);
            row.add(i + 1);
            row.add(actor.getSn());
            row.add(actor.getActor_name());
            row.add(actor.getPlatform_id());
            row.add(actor.getWx_id());
            row.add(actor.getLabel());
            row.add(actor.getHomepage_link());
            row.add(actor.getOrganization());
            row.add(actor.getAttribute_one());
            row.add(actor.getAttribute_two());
            row.add(actor.getAttribute_extension());
            row.add(actor.getQuoted_price());
            row.add(actor.getFans_num());
            row.add(actor.getProvince());
            row.add(actor.getCity());
            row.add(actor.getShipping_address());
            row.add(actor.getLinkman());
            row.add(actor.getContact_number());
            row.add(actor.getJob_wx());
            row.add(actor.getBusiness_owner());
            row.add(actor.getTotal_view());
            row.add(actor.getAverage_view());
            row.add(actor.getRemark());
            row.add(actor.getCreate_user_name());
            row.add(actor.getCreated_at().format(DateUtil.DATE_TIME_FORMATTER));
            rows.add(row);
        }
        excelData.setRows(rows);
        ExcelUtils.exportExcel(response, "达人", excelData);
    }

    @Override
    public void afterPropertiesSet() {
        setCommonMapper(actorMapper);
    }

    /**
     * 获取下个客户编号
     *
     * @param maxSn 最大客户编号
     * @return 下个客户编号
     */
    private String getNextSn(String maxSn) {
        if (!StringUtils.hasText(maxSn)) {
            return "A1000000";
        } else {
            int order = Integer.parseInt(maxSn.substring(1));
            return String.format("%s%s", maxSn.substring(0, 1), order + 1);
        }
    }

    private void initParam(ActorSearchParam param) {
        if (StringUtils.hasText(param.getIds())) {
            Integer limit = param.getLimit();
            Integer offset = param.getOffset();
            String ids = param.getIds();
            param = new ActorSearchParam();
            param.setLimit(limit);
            param.setOffset(offset);
            param.setIds(ids);
        } else {
            User user = UserToken.getContext();
            if (CodeCaption.ROLE_ADMIN != user.getRole() && !user.getRoleIds().contains(15)) {
                param.setCreate_user_id(user.getId());
            }
        }
    }
}
