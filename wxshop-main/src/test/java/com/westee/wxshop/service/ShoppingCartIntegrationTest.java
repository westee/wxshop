package com.westee.wxshop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.westee.wxshop.WxshopApplication;
import com.westee.api.data.PageResponse;
import com.westee.wxshop.entity.ShoppingCartData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WxshopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:test-application.yml"})
public class ShoppingCartIntegrationTest extends AbstractIntegrationTest{
    @Test
    public void canQueryShoppingCartData() throws JsonProcessingException {
        UserLoginResponse loginResponse = loginAndGetCookie();

        PageResponse<ShoppingCartData> response = doHttpRequest("/api/v1/shoppingCart?pageNum=2&pageSize=1",
                    true, null, loginResponse.cookie).asJsonObject(new TypeReference<PageResponse<ShoppingCartData>>() {
        });

        assertEquals(2, response.getPageNum());
        assertEquals(1, response.getPageSize());
        assertEquals(2, response.getTotalPage());
        assertEquals(1, response.getData().size());
        assertEquals(2, response.getData().get(0).getShop().getId());
    }

    @Test
    public void canAddShoppingCartData() throws JsonProcessingException {
        UserLoginResponse loginResponse = loginAndGetCookie();

        PageResponse<ShoppingCartData> response = doHttpRequest("/api/v1/shoppingCart",
                false, null, loginResponse.cookie).asJsonObject(new TypeReference<PageResponse<ShoppingCartData>>() {
        });
    }


}
