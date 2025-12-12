package com.springboot.demo.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.util.List;

/**
 * 
 * @TableName test_data
 */
@TableName(value ="test_data",autoResultMap = true)
@Data
public class TestData {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * list<String>
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> items;
}