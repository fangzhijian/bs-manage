package com.bs.manage;

import com.alibaba.druid.support.json.JSONUtils;
import com.bs.manage.code.PubCode;
import com.bs.manage.code.RiskAppealEnum;
import com.bs.manage.exception.BusinessException;
import com.bs.manage.model.bean.account.Team;
import com.bs.manage.model.bean.account.User;
import com.bs.manage.model.bean.account.UserTags;
import com.bs.manage.model.bean.erp.ReturnWarehouseEntry;
import com.bs.manage.model.bean.erp.SalesRelease;
import com.bs.manage.model.json.XBaseAiJson;
import com.bs.manage.model.param.console.BoardParam;
import com.bs.manage.until.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.*;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 2020/4/26 15:09
 * fzj
 */
@Slf4j
public class DemoTest {

//    static RestTemplate restTemplate;
//
//    static {
//        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
//        factory.setReadTimeout(5000);//单位为ms
//        factory.setConnectTimeout(5000);//单位为ms
//        restTemplate = new RestTemplate(factory);
//        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
//        messageConverters.forEach((x) -> {
//            if (x instanceof StringHttpMessageConverter) {
//                ((StringHttpMessageConverter) x).setDefaultCharset(StandardCharsets.UTF_8);
//            }
//        });
//    }

    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException, SQLException {
//        MailUtil.sendMail("绩效考核","你好，我是秦始皇！", Collections.singletonList("fangzhijian@brownsmiss.com"));
//        Class.forName("com.mysql.cj.jdbc.Driver");
//        Connection connection = DriverManager.getConnection("jdbc:mysql:///bs-manage?serverTimezone=GMT%2B8&characterEncoding=UTF-8&allowMultiQueries=true&autoReconnect=true&zeroDateTimeBehavior=convertToNull&useSSL=false",
//                "root","root");
//        connection.setAutoCommit(false);
//        connection.close();

//        WebClient webClient = WebClient.create();
//        String s = webClient.post().uri("http://localhost:8868/test1").retrieve().bodyToMono(String.class).block();
//        System.out.println(s);
        String uri = "/chat/api/chat_message/0199e6aa-2184-7670-bcd9-4736c5a74194";
        String token = "application-af30aa0d9b80aa893ef348ca6c2c87dd";
        WebClient webClient = WebClient.builder().baseUrl("http://120.26.23.172:18080")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authorization", "Bearer " + token).build();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("message", "怎么进件开户");
        map.add("stream", "false");
        map.add("re_chat", "true");

        try {
            XBaseAiJson flux = webClient.post().uri(uri).body(BodyInserters.fromFormData(map))
                    .retrieve()
                    .bodyToMono(XBaseAiJson.class)
                    .block();
        }catch (Exception e){
            String message = e.getMessage();
            if(message.contains("500 Internal Server")){
                System.out.println(message);
            }
        }



    }

    @Test
    public void test1() {
        List<Team> teams = new ArrayList<>();
        teams.add(Team.builder().id(3L).build());
        teams.add(Team.builder().id(2L).build());
        teams.add(Team.builder().id(5L).build());
        teams.add(Team.builder().id(1L).build());
        teams.add(Team.builder().id(4L).build());
        teams.sort((x, y) -> y.getId().compareTo(x.getId()));

        teams.forEach(x -> System.out.println(x.getId()));
    }


}
