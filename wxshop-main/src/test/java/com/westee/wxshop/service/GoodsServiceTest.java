package com.westee.wxshop.service;

import com.westee.api.DataStatus;
import com.westee.api.exceptions.HttpException;
import com.westee.api.data.PageResponse;
import com.westee.wxshop.generate.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoodsServiceTest {
    @Mock
    private GoodsMapper goodsMapper;
    @Mock
    private ShopMapper shopMapper;
    @Mock
    private Shop shop;
    @Mock
    private Goods goods;

    // 待测的服务
    @InjectMocks
    private GoodsService goodsService;

    @BeforeEach
    public void initUserContext(){
        User user = new User();
        user.setId(1L);
        UserContext.setCurrentUser(user);

        // 假设查找一个商店就返回一个商店; lenient防止报错,因为在不需要这行代码时也运行了；
        lenient().when(shopMapper.selectByPrimaryKey(Mockito.anyLong())).thenReturn(shop);
    }

    @AfterEach
    public void deleteUserContext(){
        UserContext.setCurrentUser(null);
    }


    @Test
    void createGoodsSuccessIfIsOwner() {
        when(shop.getOwnerUserId()).thenReturn(1L);
        when(goodsMapper.insert(goods)).thenReturn(123);

        Assertions.assertEquals(goods, goodsService.createGoods(goods));

        verify(goods).setId(123L);
    }


    @Test
    void createGoodsFailIfIsOwner() {
        // 用户id改变 ，在创建商品时应该抛出错误
        when(shop.getOwnerUserId()).thenReturn(12L);
        HttpException exception = assertThrows(HttpException.class, ()->{
            goodsService.createGoods(goods);
        });
        assertEquals(403, exception.getStatusCode());
    }

    @Test
    void throwExceptionIfGoodsNotFound() {
        long goodsToBeDeleted = 233;
        when(shop.getOwnerUserId()).thenReturn(1L);
        when(goodsMapper.selectByPrimaryKey(goodsToBeDeleted)).thenReturn(null);

        HttpException exception = assertThrows(HttpException.class, ()->{
            goodsService.deleteGoodsById(goodsToBeDeleted);
        });
        assertEquals(404, exception.getStatusCode());
    }

    @Test
    void deleteGoodsThrowExceptionIfNotOwner() {
        long goodsToBeDeleted = 233;
        when(shop.getOwnerUserId()).thenReturn(2L);

        HttpException exception = assertThrows(HttpException.class, ()->{
            goodsService.deleteGoodsById(goodsToBeDeleted);
        });
        assertEquals(401, exception.getStatusCode());
    }

    @Test
    void deleteGoodsSuccess(){
        long goodsToBeDeleted = 233;
        when(shop.getOwnerUserId()).thenReturn(1L);
        when(goodsMapper.selectByPrimaryKey(goodsToBeDeleted)).thenReturn(goods);

        goodsService.deleteGoodsById(goodsToBeDeleted);

        verify(goods).setStatus(DataStatus.DELETED.getName());
    }

    @Test
    void getGoodsSucceedWithNullShopId(){
        int pageNumber = 5;
        int pageSize = 10;

        List<Goods> mockData = Mockito.mock( List.class);

        when(goodsMapper.countByExample(any())).thenReturn(55L);
        when(goodsMapper.selectByExample(any())).thenReturn(mockData);

        PageResponse<Goods> result = goodsService.getGoods(pageNumber, pageSize, null);
        assertEquals(6, result.getTotalPage());
        assertEquals(5, result.getPageNum());
        assertEquals(10, result.getPageSize());
        assertEquals(mockData, result.getData());
    }

    @Test
    void getGoodsSucceedWithNonNullShopId(){
        int pageNumber = 5;
        int pageSize = 10;

        List<Goods> mockData = Mockito.mock( List.class);

        when(goodsMapper.countByExample(any())).thenReturn(100L);
        when(goodsMapper.selectByExample(any())).thenReturn(mockData);

        PageResponse<Goods> result = goodsService.getGoods(pageNumber, pageSize, 456);
        assertEquals(10, result.getTotalPage());
        assertEquals(5, result.getPageNum());
        assertEquals(10, result.getPageSize());
        assertEquals(mockData, result.getData());
    }

    @Test
    void updateGoodsSucceed(){
        when(shop.getOwnerUserId()).thenReturn(1L);
        when(goodsMapper.updateByExample(any(), any())).thenReturn(1);
        assertEquals(goods, goodsService.updateGoods(goods));
    }


    @Test
    void countGoods() {
    }

    @Test
    public void test(){
        System.out.println();
    }
}