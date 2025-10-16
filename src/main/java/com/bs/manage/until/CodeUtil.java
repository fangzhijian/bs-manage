package com.bs.manage.until;

import com.bs.manage.annotation.CodeAnnotation;
import com.bs.manage.code.CodeCaption;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 2020/2/18 16:48
 * fzj
 * code工具类
 */
public class CodeUtil {

    /**
     * 将所有的code和对应的中文说明放进一个map容器里面
     */
    private static Map<String, Map<Integer, String>> codeMap;

    static {
        Class<CodeCaption> codeCaptionClass = CodeCaption.class;
        Field[] fields = codeCaptionClass.getDeclaredFields();
        codeMap = new HashMap<>();
        for (Field field : fields) {
            if (field.isAnnotationPresent(CodeAnnotation.class)) {
                CodeAnnotation annotation = field.getAnnotation(CodeAnnotation.class);
                try {
                    int code = field.getInt(codeCaptionClass);
                    String category = annotation.category();
                    String value = annotation.value();
                    Map<Integer, String> captionMap = codeMap.get(category);
                    if (captionMap == null || captionMap.isEmpty()) {
                        captionMap = new HashMap<>();
                    }
                    captionMap.put(code, value);
                    codeMap.put(category, captionMap);
                } catch (IllegalAccessException e) {
                    throw new ExceptionInInitializerError();
                }
            }
        }
    }

    /**
     * @param category code对应的类别
     * @param code     code值
     * @return code对应的中文说明
     */
    public static String getCaption(String category, Integer code) {
        if (code == null) {
            return null;
        }
        if (codeMap.get(category) == null) {
            return null;
        }
        return codeMap.get(category).get(code);
    }

    /**
     * 获取code对应类型所有code
     *
     * @param category code对应的类别
     * @return code对应类型所有code
     */
    public static Map<Integer, String> getCaptionMap(String category) {
        return codeMap.get(category);
    }
}
