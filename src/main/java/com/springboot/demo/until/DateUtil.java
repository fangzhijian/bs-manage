package com.springboot.demo.until;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 时间工具类
 */
public class DateUtil {

    /**
     * 默认空值,如果是timestamp类型要大于1970-01-01 08:00:00
     */
    public static final LocalDateTime NULL = LocalDateTime.of(1997, 1, 1, 8, 0, 1);

    public static final String DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME);
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final DateTimeFormatter yyyyMM_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");
    public static final DateTimeFormatter yyyyMMdd_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

}
