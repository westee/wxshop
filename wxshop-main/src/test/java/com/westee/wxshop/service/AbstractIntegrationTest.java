package com.westee.wxshop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kevinsawicki.http.HttpRequest;
import com.westee.wxshop.entity.LoginResponse;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_OK;

public class AbstractIntegrationTest {
    @Autowired
    Environment environment;

    public static final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${spring.datasource.url}")
    private String databaseUrl;
    @Value("${spring.datasource.username}")
    private String databaseUsername;
    @Value("${spring.datasource.password}")
    private String databasePassword;

    @BeforeEach
    public void initDataBase() {
        ClassicConfiguration conf = new ClassicConfiguration();
        conf.setDataSource(databaseUrl, databaseUsername, databasePassword);
        Flyway flyway = new Flyway(conf);
        flyway.clean();
        flyway.migrate();
    }

    public String getUrl(String apiName) {
        // 获取集成测试的端口号
        return "http://localhost:" + environment.getProperty("local.server.port") + apiName;
    }

    public UserLoginResponse loginAndGetCookie() throws JsonProcessingException {
        String statusResponse = doHttpRequest("/api/status", true, null, null).body;
        LoginResponse statusResponseData = objectMapper.readValue(statusResponse, LoginResponse.class);
        Assertions.assertFalse(statusResponseData.isLogin());

        // 获取验证码
        int responseCode = doHttpRequest("/api/code", false,
                objectMapper.writeValueAsString(CheckTelServiceTest.VALID_PARAMS), null).code;
        Assertions.assertEquals(HTTP_OK, responseCode);

        // 使用验证码登录
        HttpResponse loginResponse = doHttpRequest("/api/login", false,
                objectMapper.writeValueAsString(CheckTelServiceTest.VALID_PARAMS_CODE), null);
        List<String> setCookie = loginResponse.headers.get("Set-Cookie");

        // 得到cookie
        String cookie = getSessionIdFromSetCookie(setCookie.stream().filter(c -> c.contains("JSESSIONID"))
                .findFirst()
                .get());
        statusResponse = doHttpRequest("/api/status", true, null, cookie).body;
        statusResponseData = objectMapper.readValue(statusResponse, LoginResponse.class);
        return new UserLoginResponse(cookie, statusResponseData.getUser());
    }

    // 获取cookie
    private String getSessionIdFromSetCookie(String session) {
        int semiColonIndex = session.indexOf(";");
        return session.substring(0, semiColonIndex);
    }

    public static class UserLoginResponse {
        String cookie;
        User user;

        public UserLoginResponse() {
        }

        public UserLoginResponse(String cookie, User user) {
            this.cookie = cookie;
            this.user = user;
        }
    }

    public static class HttpResponse {
        int code;
        String body;
        Map<String, List<String>> headers;

        public HttpResponse(int code, String body, Map<String, List<String>> headers) {
            this.code = code;
            this.body = body;
            this.headers = headers;
        }

        public <T>  T asJsonObject(TypeReference<T> data) throws JsonProcessingException {
            T result = objectMapper.readValue(body, data);
            return result;
        }
    }

    public HttpResponse doHttpRequest(String apiName, boolean isGet, String requestBody, String cookie) throws JsonProcessingException {
        System.out.println(requestBody);
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
            request.send( requestBody );
        }
        return new HttpResponse(request.code(), request.body(), request.headers());
    }
}
