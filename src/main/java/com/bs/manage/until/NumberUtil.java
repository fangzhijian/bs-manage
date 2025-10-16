package com.bs.manage.until;

import java.math.BigDecimal;

/**
 * 2020/2/25 11:27
 * fzj
 */
public class NumberUtil {

    /**
     * 一百
     */
    public static BigDecimal HUNDRED = new BigDecimal(100);
    /**
     * 万
     */
    public static BigDecimal TEN_THOU = new BigDecimal(10000);

    /**
     * @param number 数字基本类型或对象
     * @return 数字是否为空或者等于0
     */
    public static boolean isBlank(Number number) {
        return number == null || number.doubleValue() == 0;
    }

    /**
     * @param number 数字基本类型或对象
     * @return 数字是否为空或者等于0
     */
    public static boolean isNotBlank(Number number) {
        return number != null && number.doubleValue() != 0;
    }


    /**
     * 获取相除结果
     *
     * @param divisor   除数
     * @param dividend  被除数
     * @param zeroValue 被除数为0时的默认值
     * @return 除后的结果
     */
    public static BigDecimal getAverageBigDecimal(Number divisor, Number dividend, int zeroValue) {
        if (dividend.intValue() <= 0) {
            return new BigDecimal(zeroValue);
        } else {
            return BigDecimal.valueOf(divisor.doubleValue()).divide(BigDecimal.valueOf(dividend.doubleValue()), 2, BigDecimal.ROUND_HALF_UP);
        }

    }

    /**
     * 获取相除结果
     *
     * @param divisor   除数
     * @param dividend  被除数
     * @param zeroValue 被除数为0时的默认值
     * @return 除后的结果
     */
    public static String getAverageString(Number divisor, Number dividend, String zeroValue) {
        if (dividend.intValue() <= 0) {
            return zeroValue;
        } else {
            return BigDecimal.valueOf(divisor.doubleValue()).divide(BigDecimal.valueOf(dividend.doubleValue()), 0, BigDecimal.ROUND_HALF_UP).toString();
        }

    }

    /**
     * 获取相除结果
     *
     * @param divisor   除数
     * @param dividend  被除数
     * @param zeroValue 被除数为0时的默认值
     * @return 除后的结果
     */
    public static BigDecimal getPercentBigDecimal(Number divisor, Number dividend, int zeroValue) {
        if (dividend.doubleValue() <= 0) {
            return new BigDecimal(zeroValue);
        } else {
            return BigDecimal.valueOf(divisor.doubleValue() * 100).divide(BigDecimal.valueOf(dividend.doubleValue()), 2, BigDecimal.ROUND_HALF_UP);
        }

    }


    /**
     * 获取相除结果
     *
     * @param divisor   除数
     * @param dividend  被除数
     * @param zeroValue 被除数为0时的默认值
     * @return 除后的结果
     */
    public static String getPercentString(Number divisor, Number dividend, String zeroValue) {
        if (dividend.doubleValue() <= 0) {
            return zeroValue;
        } else {
            return BigDecimal.valueOf(divisor.doubleValue() * 100).divide(BigDecimal.valueOf(dividend.doubleValue()), 2, BigDecimal.ROUND_HALF_UP).toString();
        }

    }

    /**
     * 元转为万元
     *
     * @param amount 金额
     * @return 万元金额
     */
    public static BigDecimal toTenThouAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        } else if (amount.compareTo(TEN_THOU) < 0) {
            return amount.divide(NumberUtil.TEN_THOU, 4, BigDecimal.ROUND_HALF_UP);
        } else {
            return amount.divide(NumberUtil.TEN_THOU, 0, BigDecimal.ROUND_HALF_UP);
        }
    }
}
