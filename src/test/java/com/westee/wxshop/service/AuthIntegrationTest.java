package com.westee.wxshop.service;

import com.github.kevinsawicki.http.HttpRequest;
import com.westee.wxshop.WxshopApplication;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.westee.wxshop.entity.LoginResponse;
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

import java.util.List;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WxshopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:test-application.yml"})
public class AuthIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void loginLogoutTest() throws JsonProcessingException {
        // 得到cookie
        String sessionId = loginAndGetCookie().cookie;

        // 此时应该为登录状态
        String statusResponse = doHttpRequest("/api/status", true, null, sessionId).body;

        // 将statusResponse读取成LoginResponse。
        LoginResponse response = objectMapper.readValue(statusResponse, LoginResponse.class);
        Assertions.assertTrue(response.isLogin());
        Assertions.assertNotNull(CheckTelServiceTest.VALID_PARAMS.getTel(), response.getUser().getTel());

        // 注销登录
        doHttpRequest("/api/logout", false, null, sessionId);

        // 未登录状态
        statusResponse = doHttpRequest("/api/status", true, null, sessionId).body;
        response = objectMapper.readValue(statusResponse, LoginResponse.class);
        Assertions.assertFalse(response.isLogin());
    }

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
    public void returnHttpBadRequestWhenParamsIsCorrect() throws JsonProcessingException {
        int responseCode = HttpRequest.post(getUrl("/api/code"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .send(objectMapper.writeValueAsString(CheckTelServiceTest.EMPTY_PARAMS))
                .code();
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), responseCode);
    }

    @Test
    public void returnUnauthorizedIfNotLogin() throws JsonProcessingException {
            int responseCode = HttpRequest.post(getUrl("/api/any"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .send(objectMapper.writeValueAsString(CheckTelServiceTest.EMPTY_PARAMS))
                .code();
        Assertions.assertEquals(HTTP_UNAUTHORIZED, responseCode);
    }
}