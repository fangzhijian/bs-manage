package com.bs.manage.config;

import com.bs.manage.intercepter.TokenInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 2018/10/30 16:59
 * fzj
 */
@Configuration
public class MyMvcConfig implements WebMvcConfigurer {

    /**
     * 是否开启token验证
     */
    @Value("${token-check}")
    private Boolean tokenCheck;

    private final TokenInterceptor tokenInterceptor;

    public MyMvcConfig(TokenInterceptor tokenInterceptor) {
        this.tokenInterceptor = tokenInterceptor;
    }


    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/druid");
    }

    //静态文件映射
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resources/")
                .addResourceLocations("classpath:/resources/");
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }


    //跨域配置
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedMethods("*")
                .allowedOriginPatterns("*")
                .allowedHeaders("*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (tokenCheck){
            registry.addInterceptor(tokenInterceptor).addPathPatterns("/admin/**/*")
                    .excludePathPatterns("/admin/login","/admin/logout");
        }
    }
}
