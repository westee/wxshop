package com.westee.wxshop.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.westee.wxshop.WxshopApplication;
import com.westee.wxshop.entity.Response;
import com.westee.wxshop.generate.Goods;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.HttpServletResponse;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WxshopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:test-application.yml"})
public class GoodsIntegrationTest extends AbstractIntegrationTest {


    @Test
    public void testCreateGoods() throws JsonProcessingException {
        Goods goods = new Goods();
        goods.setName("名字");
        goods.setDescription("描述");
        goods.setDetails("细节");
        goods.setImgUrl("baidu");
        goods.setPrice(100L);
        goods.setStock(10);
        goods.setShopId(1L);

        String cookie = loginAndGetCookie();

        HttpResponse response = doHttpRequest("/api/v1/goods", false, objectMapper.writeValueAsString(goods) , cookie);
        Response<Goods> data = objectMapper.readValue(response.body, new TypeReference<Response<Goods>>() {
        });
//        Assertions.assertEquals(HttpServletResponse.SC_CREATED, response.code);
        Assertions.assertEquals("名字", data.getData().getName());
    }

    @Test
    public void testDeleteGoods() {

    }
}
