package com.bs.manage.controller;

import com.bs.manage.mapper.account.UserMapper;
import com.bs.manage.model.bean.account.User;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.model.json.XBaseAiJson;
import com.bs.manage.model.json.XBaseStreamJson;
import jakarta.annotation.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

/**
 *
 * @author fangzhijian
 * @Desc
 * @date 2025/10/11 13:34
 */
@RestController
public class TestController {

    @Resource
    private UserMapper userMapper;

    @GetMapping(value = "/test")
    public XBaseAiJson test(){
//        String uri = "/chat/api/open";
        String uri = "/chat/api/chat_message/0199dce2-f617-7c12-a875-6309facd4457";
        String token = "application-af30aa0d9b80aa893ef348ca6c2c87dd";
        WebClient webClient = WebClient.builder().baseUrl("http://120.26.23.172:18080")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authorization","Bearer "+token).build();

        MultiValueMap<String,String> map = new LinkedMultiValueMap<>();
        map.add("message","怎么进件开户");
        map.add("stream","false");
        map.add("re_chat","true");

        XBaseAiJson flux = webClient.post().uri(uri).body(BodyInserters.fromFormData(map))
                .retrieve().bodyToMono(XBaseAiJson.class).block();
        return flux;
    }

    @GetMapping("test1")
    @Transactional
    public ResponseJson test1(){
        userMapper.update(User.builder().id(1L).team_id(1L).build());
        setDate();
        System.out.println(10/0);
        return new ResponseJson().setCode(500).setMsg("坏了");
    }

    private void setDate(){
        userMapper.update(User.builder().id(2L).team_id(1L).build());
    }
}
