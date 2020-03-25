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

    @Test
    public void loginLogoutTest() throws JsonProcessingException {
        String statusResponse  = HttpRequest.post(getUrl("/api/status"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body();
        Map<String, Object>  response = objectMapper.readValue(statusResponse, Map.class);
        Assertions.assertFalse((Boolean) response.get("login"));

        int responseCode = HttpRequest.post(getUrl("/api/code"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .send(objectMapper.writeValueAsString(CheckTelServiceTest.VALID_PARAMS))
                .code();
        Assertions.assertEquals(HTTP_OK, responseCode);

        Map<String, List<String>> responseHeaders  = HttpRequest.post(getUrl("/api/login"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .send(objectMapper.writeValueAsString(CheckTelServiceTest.VALID_PARAMS_CODE))
                .headers();
        List<String> setCookie = responseHeaders.get("Set-Cookie");
        Assertions.assertNotNull(setCookie);
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