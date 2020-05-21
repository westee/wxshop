package com.westee.wxshop.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.westee.wxshop.WxshopApplication;
import com.westee.wxshop.entity.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WxshopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:test-application.yml"})
public class GoodsIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void testCreateGoods() throws JsonProcessingException {
        UserLoginResponse loginResponse = loginAndGetCookie();

        Shop shop = new Shop();
        shop.setName("微信店铺");
        shop.setDescription("我的微信店铺");
        shop.setImgUrl("www.baidu.com");

        HttpResponse shopResponse = doHttpRequest("/api/v1/shop",
                false,
                objectMapper.writeValueAsString(shop),
                loginResponse.cookie);

        Response<Shop> shopData = objectMapper.readValue(shopResponse.body, new TypeReference<Response<Shop>>() {
        });

        assertEquals(SC_CREATED, shopResponse.code);
        assertEquals("微信店铺", shopData.getData().getName());
        assertEquals("我的微信店铺", shopData.getData().getDescription());
        assertEquals("www.baidu.com", shopData.getData().getImgUrl());
        assertEquals("ok", shopData.getData().getStatus());
        assertEquals(shopData.getData().getOwnerUserId(), loginResponse.user.getId());

        Goods goods = new Goods();
        goods.setName("名字");
        goods.setDescription("描述");
        goods.setDetails("细节");
        goods.setImgUrl("baidu");
        goods.setPrice(100L);
        goods.setStock(10);
        goods.setShopId(shopData.getData().getId());

        HttpResponse response = doHttpRequest("/api/v1/goods", false, objectMapper.writeValueAsString(goods), loginResponse.cookie);
        Response<Goods> goodsResponse = objectMapper.readValue(response.body, new TypeReference<Response<Goods>>() {
        });

        assertEquals(SC_CREATED, response.code);
        assertEquals("名字", goodsResponse.getData().getName());
        assertEquals(shopData.getData().getId(), goodsResponse.getData().getShopId());
        assertEquals("ok", goodsResponse.getData().getStatus());
    }

    @Test
    public void testDeleteGoods() {

    }
}
