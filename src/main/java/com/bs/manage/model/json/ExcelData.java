package com.bs.manage.model.json;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 2018/12/24 19:31
 * 走路呼呼带风
 */
@Data
public class ExcelData implements Serializable{

    private static final long serialVersionUID = 8414040429498516360L;

    private String[] columnNames;         //表头
    private List<List<Object>> rows;      //数据
    private String sheetName = "Sheet1";  //页签名称
    private String excelName;             //excel名称

}
