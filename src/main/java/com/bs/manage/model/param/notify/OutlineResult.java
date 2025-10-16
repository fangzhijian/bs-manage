package com.bs.manage.model.param.notify;

import lombok.Data;

import java.io.Serializable;

/**
 * 2020/4/14 11:12
 * fzj
 */
@Data
public class OutlineResult implements Serializable {

    private static final long serialVersionUID = 6116645025484509821L;

    private int total;              //总未读数
    private int customer_auth;      //客户授权过期通知未读数
    private int customer_release;   //客户待释放通知未读数
    private int kpi;                //考核通知未读数
}
