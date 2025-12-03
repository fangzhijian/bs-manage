

package com.springboot.demo.until;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

/**
 * 解析speL 表达式
 */
public class SpeLUtil {

    /**
     * 支持 #p0 参数索引的表达式解析
     *
     * @param joinPoint 切点
     * @param speL      表达式
     * @return 解析后的字符串
     */
    public static String parse(ProceedingJoinPoint joinPoint, String speL) {
        if (StrUtil.isBlank(speL)) {
            return StrUtil.EMPTY;
        }
        if (!speL.startsWith("#")) {
            return speL;
        }
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method targetMethod = methodSignature.getMethod();
        Object target = joinPoint.getTarget();
        Object[] arguments = joinPoint.getArgs();
        //获取被拦截方法参数名列表(使用Spring支持类库)
        StandardReflectionParameterNameDiscoverer standardReflectionParameterNameDiscoverer = new StandardReflectionParameterNameDiscoverer();
        String[] paraNameArr = standardReflectionParameterNameDiscoverer.getParameterNames(targetMethod);
        if (ArrayUtil.isEmpty(paraNameArr)) {
            return speL;
        }
        //使用speL进行key的解析
        ExpressionParser parser = new SpelExpressionParser();
        //speL上下文
        StandardEvaluationContext context = new MethodBasedEvaluationContext(target, targetMethod, arguments, standardReflectionParameterNameDiscoverer);
        //把方法参数放入speL上下文中
        for (int i = 0; i < paraNameArr.length; i++) {
            context.setVariable(paraNameArr[i], arguments[i]);
        }
        return parser.parseExpression(speL).getValue(context, String.class);
    }
}
