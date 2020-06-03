package com.westee.wxshop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.westee.api.DataStatus;
import com.westee.api.data.GoodsInfo;
import com.westee.api.data.OrderInfo;
import com.westee.api.generate.Order;
import com.westee.wxshop.WxshopApplication;
import com.westee.wxshop.entity.GoodsWithNumber;
import com.westee.wxshop.entity.OrderResponse;
import com.westee.wxshop.entity.Response;
import com.westee.wxshop.generate.Goods;
import com.westee.wxshop.mock.MockOrderRpcService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WxshopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:test-application.yml"})
public class OrderIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    MockOrderRpcService mockOrderRpcService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(mockOrderRpcService);

        Mockito.when(mockOrderRpcService.orderRpcService.createOrder(Mockito.any(), Mockito.any()))
                .thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                        Order order = invocationOnMock.getArgument(1);
                        order.setId(123L);
                        return order;
                    }
                });
    }

    @Test
    public void canCreateOrder() throws Exception {
        UserLoginResponse loginResponse = loginAndGetCookie();
        OrderInfo orderInfo = new OrderInfo();
        GoodsInfo goodsInfo1 = new GoodsInfo();
        GoodsInfo goodsInfo2 = new GoodsInfo();

        goodsInfo1.setId(4);
        goodsInfo1.setNumber(3);
        goodsInfo2.setId(5);
        goodsInfo2.setNumber(5);

        orderInfo.setGoods(Arrays.asList(goodsInfo1, goodsInfo2));

        Response<OrderResponse> response = doHttpRequest("/api/v1/order", false, orderInfo.toString(),
                loginResponse.getCookie())
                .asJsonObject(new TypeReference<Response<OrderResponse>>() {
                });

        Assertions.assertEquals(123L, response.getData().getId());

        Assertions.assertEquals(2l, response.getData().getShop().getId());
        Assertions.assertEquals("shop2", response.getData().getShop().getName());
        Assertions.assertEquals(DataStatus.PENDING.getName(), response.getData().getStatus());
        Assertions.assertEquals("火星", response.getData().getAddress());
        Assertions.assertEquals(Arrays.asList(4l, 5l),
                response.getData().getGoods().stream().map(Goods::getId)
                        .collect(Collectors.toList())
        );

        Assertions.assertEquals(Arrays.asList(3, 5),
                response.getData().getGoods().stream().map(GoodsWithNumber::getNumber)
                        .collect(Collectors.toList())
        );

    }

    @Test
    public void canRollBackIfDeductStockFailed() throws JsonProcessingException {
        UserLoginResponse loginResponse = loginAndGetCookie();
        OrderInfo orderInfo = new OrderInfo();
        GoodsInfo goodsInfo1 = new GoodsInfo();
        GoodsInfo goodsInfo2 = new GoodsInfo();

        goodsInfo1.setId(4);
        goodsInfo1.setNumber(3);
        goodsInfo2.setId(5);
        goodsInfo2.setNumber(6);

        orderInfo.setGoods(Arrays.asList(goodsInfo1, goodsInfo2));


        HttpResponse response = doHttpRequest("/api/v1/order", false, orderInfo.toString(),
                loginResponse.getCookie());

        Assertions.assertEquals(HttpStatus.GONE.value(), response.code);
    }


}
