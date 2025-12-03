package com.springboot.demo.config;

import com.github.houbb.heaven.constant.CharConst;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.github.houbb.sensitive.word.support.check.WordChecks;
import com.github.houbb.sensitive.word.support.ignore.SensitiveWordCharIgnores;
import com.github.houbb.sensitive.word.support.replace.WordReplaceChar;
import com.github.houbb.sensitive.word.support.resultcondition.WordResultConditions;
import com.github.houbb.sensitive.word.support.tag.WordTags;
import com.github.houbb.sensitive.word.utils.InnerStreamUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * {@code @description}
 * 敏感词过滤配置
 *
 * @author fangzhijian
 * @since 2025-12-01 11:39
 */
@Configuration
public class SensitiveWordConfig {

    @Bean
    public SensitiveWordBs sensitiveWordBs() {
        SensitiveWordBs sensitiveWordBs = SensitiveWordBs.newInstance()
                .ignoreCase(true) //忽略大小写
                .ignoreWidth(true) //忽略半角圆角
                .ignoreNumStyle(true)  //忽略数字的写法
                .ignoreChineseStyle(true) //忽略中文的书写格式
                .ignoreEnglishStyle(true) //忽略英文的书写格式
                .ignoreRepeat(true) //忽略重复词
                .enableNumCheck(false) //是否启用数字检测，用于过滤指定长度的手机QQ微信号
                .numCheckLen(8) //数字检测，自定义指定长度，enableNumCheck为true生效
                .enableEmailCheck(false) //是有启用邮箱检测
                .enableUrlCheck(false) //是否启用链接检测
                .enableIpv4Check(false) //是否启用IPv4检测
                .enableWordCheck(true) //是否启用敏感单词检测
                .wordFailFast(true) //敏感词匹配模式是否快速返回
                .wordReplace(new WordReplaceChar(CharConst.STAR)) //设置替换策略
                .wordCheckNum(WordChecks.num()) //数字检测策略
                .wordCheckEmail(WordChecks.email()) //词对应的标签
                .wordCheckUrl(WordChecks.url()) //忽略的字符
                .wordCheckIpv4(WordChecks.ipv4()) //ipv4检测策略
                .wordCheckWord(WordChecks.word()) //敏感词检测策略
                .wordTag(WordTags.none()) //词对应的标签
                .charIgnore(SensitiveWordCharIgnores.defaults()) //忽略的字符
                .wordResultCondition(WordResultConditions.alwaysTrue()) //针对匹配的敏感词额外加工，比如可以限制英文单词必须全匹配
                .init();
        //添加自定义敏感词库
        List<String> denyWords = InnerStreamUtils.readAllLines("/static/sensitive_word_custom_dict.txt");
        sensitiveWordBs.addWord(denyWords);
        return sensitiveWordBs;
    }
}
