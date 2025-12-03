package com.springboot.demo.model.json;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 2018/12/24 19:31
 * 走路呼呼带风
 */
@Data
public class ExcelData implements Serializable{

    @Serial
    private static final long serialVersionUID = 1L;

    private String[] columnNames;         //表头
    private List<List<Object>> rows;      //数据
    private String sheetName = "Sheet1";  //页签名称
    private String excelName;             //excel名称

}
