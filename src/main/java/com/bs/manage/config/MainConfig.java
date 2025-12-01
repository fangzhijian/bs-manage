package com.bs.manage.config;

import com.bs.manage.until.DateUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * 2020/1/14 15:41
 * fzj
 */
@Configuration
@MapperScan(basePackages = "com.bs.manage.mapper", annotationClass = Repository.class)
public class MainConfig {

    /**
     * 设置jackson的objectMapper格式
     */
    private ObjectMapper initObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        // 空的字段也返回
        objectMapper.setDefaultPropertyInclusion(JsonInclude.Include.ALWAYS);
        // 反序列化时候遇到不匹配的属性并不抛出异常
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 序列化时候遇到空对象不抛出异常
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 反序列化的时候如果是无效子类型,不抛出异常
        objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        // 支持jdk8时间格式分别为yyyy-MM-dd HH:mm:ss,yyyy-MM-dd,HH:mm:ss
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        objectMapper.registerModule(javaTimeModule).registerModule(new ParameterNamesModule()).registerModule(new Jdk8Module());
        return objectMapper;
    }

    /**
     * jackson不带记录全类名,可以被其他语言解析
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return this.initObjectMapper();
    }


    /**
     * json记录全类名,可以被通用Object类反序列化
     */
    @Bean(name = "commonObjectMapper")
    public ObjectMapper commonObjectMapper() {
        ObjectMapper objectMapper = this.initObjectMapper();
        //记录class,使之能反序列各种复杂结构
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL
                , JsonTypeInfo.As.WRAPPER_ARRAY);
        objectMapper.findAndRegisterModules();
        return objectMapper;
    }

    /**
     * 修改RedisTemplate使用jackson序列化
     * redis key值使用String类型,操作hash时map的key值也要使用String
     */
    @Bean(name = "redisTemplate")
    @Primary
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory,
                                                       @Qualifier("commonObjectMapper") ObjectMapper objectMapper) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setEnableTransactionSupport(true);
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        //默认会在spring事务内开启redis事务,导致get不到值,但是有事务。
        template.setEnableTransactionSupport(false);
        return template;
    }


    /**
     * spring缓存使用Redis
     */
    @Bean
    public RedisCacheManager redisCacheManager(RedisTemplate<String, Object> redisTemplate, RedisConnectionFactory redisConnectionFactory) {
        return new RedisCacheManager(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory),
                //未指定cacheNames使用默认时间60小时
                this.getRedisCacheConfigurationWithTtl(3600L * 60, redisTemplate),
                //指定cacheNames自定义过期时间
                this.getRedisCacheConfigurationMap(redisTemplate));
    }

    /**
     * 修改特定cacheNames的缓存时间
     */
    private Map<String, RedisCacheConfiguration> getRedisCacheConfigurationMap(RedisTemplate<String, Object> redisTemplate) {
        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();
        redisCacheConfigurationMap.put("demo", this.getRedisCacheConfigurationWithTtl(3000L, redisTemplate));
        return redisCacheConfigurationMap;
    }

    /**
     * redis缓存配置
     */
    private RedisCacheConfiguration getRedisCacheConfigurationWithTtl(Long seconds, RedisTemplate<String, Object> redisTemplate) {
        return RedisCacheConfiguration.defaultCacheConfig()
                //使用RedisTemplate的Jackson序列化
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisTemplate.getValueSerializer()))
                .disableCachingNullValues()
                //过期时间
                .entryTtl(Duration.ofSeconds(seconds))
                //统一使用前缀cache,格式 cache:{cacheNames}:{key}
                .computePrefixWith((x) -> String.format("cache:%s:", x));
    }

    /**
     * 上传文件的临时目录
     * 不手动设置时如果超过10天未使用,默认目录会被linux删除,从而导致无法上传文件
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        String location = System.getProperty("user.dir") + "/data/tmp";
        File tmpFile = new File(location);
        if (!tmpFile.exists()) {
            tmpFile.mkdirs();
        }
        factory.setLocation(location);
        return factory.createMultipartConfig();
    }

    /**
     * 入参yyyy-MM-dd HH:mm:ss接收LocalDateTime
     */
    @Bean
    public Converter<String, LocalDateTime> LocalDateTimeConvert() {
        return new Converter<String, LocalDateTime>() {
            @Nullable
            @Override
            public LocalDateTime convert(@Nullable String s) {
                if (!StringUtils.hasText(s)) {
                    return null;
                }
                return LocalDateTime.parse(s, DateUtil.DATE_TIME_FORMATTER);
            }
        };
    }

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        return new RestTemplate(factory);
    }

    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(150000); // ms
        factory.setConnectTimeout(150000); // ms
        return factory;
    }

}
