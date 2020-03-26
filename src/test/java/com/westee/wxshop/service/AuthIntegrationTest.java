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


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WxshopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.yml")
public class AuthIntegrationTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    Environment environment;

    private static class HttpResponse {
        int code;
        String body;
        Map<String, List<String>> headers;

        public HttpResponse(int code, String body, Map<String, List<String>> headers) {
            this.code = code;
            this.body = body;
            this.headers = headers;
        }
    }

    private HttpResponse doHttpRequest(String apiName, boolean isGet, String requestBody, String cookie) {
        HttpRequest request;
        if (isGet) {
            request = HttpRequest.get(getUrl(apiName));
        } else {
            request = HttpRequest.post(getUrl(apiName));
        }
        if (cookie != null) {
            request.header("Cookie", cookie);
        }
        request = request.contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        if (requestBody != null) {
            request.send(requestBody);
        }
        return new HttpResponse(request.code(), request.body(), request.headers());
    }

    @Test
    public void loginLogoutTest() throws JsonProcessingException {
        // 默认情况下为未登录状态
        String statusResponse = doHttpRequest("/api/status", true, null, null).body;
//                HttpRequest.get(getUrl("/api/status"))
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .accept(MediaType.APPLICATION_JSON_VALUE)
//                .body();
        LoginResponse response = objectMapper.readValue(statusResponse, LoginResponse.class);
        Assertions.assertFalse(response.isLogin());

        // 获取验证码
        int responseCode = HttpRequest.post(getUrl("/api/code"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .send(objectMapper.writeValueAsString(CheckTelServiceTest.VALID_PARAMS))
                .code();
        Assertions.assertEquals(HTTP_OK, responseCode);

        // 登录
        Map<String, List<String>> responseHeaders = HttpRequest.post(getUrl("/api/login"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .send(objectMapper.writeValueAsString(CheckTelServiceTest.VALID_PARAMS_CODE))
                .headers();
        List<String> setCookie = responseHeaders.get("Set-Cookie");

        // 得到cookie
        String sessionId = getSessionIdFromSetCookie(setCookie.stream().filter(cookie -> cookie.contains("JSESSIONID"))
                .findFirst()
                .get());

        // 此时应该为登录状态
        statusResponse = HttpRequest.get(getUrl("/api/status"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Cookie", sessionId)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body();
        response = objectMapper.readValue(statusResponse, LoginResponse.class);
        Assertions.assertTrue(response.isLogin());
        Assertions.assertNotNull(CheckTelServiceTest.VALID_PARAMS.getTel(), response.getUser().getTel());

        // 注销登录
        HttpRequest.post(getUrl("/api/logout"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Cookie", sessionId)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .headers();

        // 未登录状态
        statusResponse = HttpRequest.get(getUrl("/api/status"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Cookie", sessionId)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body();
        response = objectMapper.readValue(statusResponse, LoginResponse.class);
        Assertions.assertFalse(response.isLogin());
    }

    private String getSessionIdFromSetCookie(String session) {
        int semiColonIndex = session.indexOf(";");
//        int equalIndex = session.indexOf("=");
        return session.substring(0, semiColonIndex);
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