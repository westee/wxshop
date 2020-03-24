package com.westee.wxshop.service;

import com.github.kevinsawicki.http.HttpRequest;
import com.westee.wxshop.WxshopApplication;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WxshopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.yml")
public class AuthIntegrationTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    Environment environment;

    @Test
    public void returnHttpOKWhenParamsIsCorrect() throws JsonProcessingException {
       int responseCode = HttpRequest.post(getUrl("/api/code"))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .send(objectMapper.writeValueAsString(CheckTelServiceTest.VALID_PARAMS))
            .code();
        Assertions.assertEquals(HttpStatus.OK.value(), responseCode);
    }

    @Test
    public void returnHttpBadRequstWhenParamsIsCorrect() throws JsonProcessingException {
        int responseCode = HttpRequest.post(getUrl("/api/code"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .send(objectMapper.writeValueAsString(CheckTelServiceTest.EMPTY_PARAMS))
                .code();
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), responseCode);
    }


    private String getUrl(String apiName) {
        // 获取集成测试的端口号
        return "http://localhost:" + environment.getProperty("local.server.port") + apiName;
    }

}