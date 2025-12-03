package com.springboot.demo.until;


import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Base64;

/**
 * Md5工具类
 */
public class Md5Util {

    /**
     * 加密值所使用的文字,最少14个
     */
    private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g','h','i','j','k'};

    /**
     * 盐值
     */
    private static final String SALT = "年终奖只够买十斤猪肉";

    /**
     * 账号默认密码
     */
    public static final String DEFAULT_PASSWORD = Md5Util.md5("123456");

    /**
     * 用户base64加密解密的编码,随机找种不常见的编码
     */
    private static final Charset DEFAULT_ENCODER_CHAR = Charset.forName("IBM866");

    /**
     * 用于拼接apiToken, P加指定密文开头,因为P不在DIGITS中
     */
    private static final String DEFAULT_START = String.format("P%s", Base64.getEncoder().encodeToString("酸菜黑鱼".getBytes(DEFAULT_ENCODER_CHAR)));

    /**
     * token格式正则表达式
     * 其中[A-?|1-9]是 char[] DIGITS中含有的的值
     */
    public static final String TOKEN_MATCHERS = "^[A-K|0-9]+" + Md5Util.DEFAULT_START + ".+";

    /**
     * md5
     *
     * @param text 明文
     */
    public static String md5(String text) {
        if (text == null) {
            return null;
        }
        MessageDigest msgDigest;
        try {
            msgDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5加密异常");
        }
        String salt = String.format("$%s$%s", SALT, text);
        msgDigest.update(salt.getBytes(Charset.forName("UTF-8")));
        byte[] bytes = msgDigest.digest();
        String md5Str = new String(encodeHex(bytes));
        return md5Str.toUpperCase();
    }


    /**
     * 验证
     *
     * @param text 明文
     * @param sign 签名
     */
    public static boolean verify(String text, String sign) {
        if (sign == null || text == null) {
            return false;
        }
        return sign.equals(md5(text));
    }


    /**
     * 16进制编码
     */
    private static char[] encodeHex(byte[] data) {
        int l = data.length;
        char[] out = new char[l << 1];
        int j = 0;
        for (byte aData : data) {
            out[j++] = DIGITS[(0xF0 & aData) >>> 4];
            out[j++] = DIGITS[0x0F & aData];
        }
        return out;
    }

    /**
     * @param apiToken 数据库中固定的token
     * @return 固定的token加上指定值再加时间
     */
    public static String encode64TokenForDateTime(String apiToken) {
        String dateTime = LocalDateTime.now().format(DateUtil.DATE_TIME_FORMATTER);
        return String.format("%s%s%s", apiToken, DEFAULT_START, Base64.getEncoder().encodeToString(dateTime.getBytes(DEFAULT_ENCODER_CHAR)));
    }

    /**
     * 获取原生token
     *
     * @param encode64Token 加过时间的apiToken
     * @return 原生token, 数据库中保存的
     */
    public static String getOriginToken(String encode64Token) {
        return encode64Token.substring(0, encode64Token.indexOf(DEFAULT_START));
    }

    /**
     * 从apiToken中获取时间
     *
     * @param encode64Token 加过时间的apiToken
     * @return 时间
     * @throws DateTimeParseException 时间格式错误
     */
    public static LocalDateTime decodeTokenForDateTime(String encode64Token) throws DateTimeParseException {
        if (!encode64Token.contains(DEFAULT_START)) {
            return null;
        }
        String dateTimeEncode = encode64Token.substring(encode64Token.indexOf(DEFAULT_START) + DEFAULT_START.length());
        String dateTime = new String(Base64.getDecoder().decode(dateTimeEncode), DEFAULT_ENCODER_CHAR);
        return LocalDateTime.parse(dateTime, DateUtil.DATE_TIME_FORMATTER);
    }

}
