package com.bs.manage.model.bean.console;

import com.bs.manage.model.bean.common.CommonModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.Alias;

import java.util.List;

/**
 * 2020/3/12 18:57
 * fzj
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("bussProcessSummary")
@SuperBuilder
@NoArgsConstructor
public class BussProcessSummary extends CommonModel {

    private static final long serialVersionUID = 4119475553872328776L;

    private Long user_id;                        //账号id
    private Long team_id;                        //团队id
    private Long customer_category_id;           //客户一级属性id
    private Integer summary_month;               //汇总月份yyyyMM
    private Integer cover_s;                     //客户分层S-覆盖数
    private Integer cover_t;                     //客户分层T-覆盖数
    private Integer cover_y;                     //客户分层Y-覆盖数
    private Integer cover_c;                     //客户分层C-覆盖数
    private Integer visit_s;                     //客户分层S-拜访数
    private Integer visit_t;                     //客户分层T-拜访数
    private Integer visit_y;                     //客户分层Y-拜访数
    private Integer visit_c;                     //客户分层C-拜访数
    private Integer deal_s;                      //客户分层S-成交数
    private Integer deal_t;                      //客户分层T-成交数
    private Integer deal_y;                      //客户分层Y-成交数
    private Integer deal_c;                      //客户分层C-成交数
    private Integer active_s;                    //客户分层S-活跃数
    private Integer active_t;                    //客户分层T-活跃数
    private Integer active_y;                    //客户分层Y-活跃数
    private Integer active_c;                    //客户分层C-活跃数
    private Integer release_s;                   //客户分层S-释放数
    private Integer release_t;                   //客户分层T-释放数
    private Integer release_y;                   //客户分层Y-释放数
    private Integer release_c;                   //客户分层C-释放数
    private Integer release_visit_s;             //客户分层S-未拜访释放数
    private Integer release_visit_t;             //客户分层T-未拜访释放数
    private Integer release_visit_y;             //客户分层Y-未拜访释放数
    private Integer release_visit_c;             //客户分层C-未拜访释放数
    private Integer release_deal_s;              //客户分层S-未成交释放数
    private Integer release_deal_t;              //客户分层T-未成交释放数
    private Integer release_deal_y;              //客户分层Y-未成交释放数
    private Integer release_deal_c;              //客户分层C-未成交释放数
    private Integer release_repeat_s;            //客户分层S-未复购释放数
    private Integer release_repeat_t;            //客户分层T-未复购释放数
    private Integer release_repeat_y;            //客户分层Y-未复购释放数
    private Integer release_repeat_c;            //客户分层C-未复购释放数

    private List<Long> userIds;                  //账号id列表
}
