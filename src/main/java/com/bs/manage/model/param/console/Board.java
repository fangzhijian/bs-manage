package com.bs.manage.model.param.console;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 2020/3/16 12:24
 * fzj
 */
@Data
public class Board implements Serializable {

    private static final long serialVersionUID = 137687327608662255L;

    private String title;                    //标题
    private List<BoardContent> contentList;  //内容列表
}
