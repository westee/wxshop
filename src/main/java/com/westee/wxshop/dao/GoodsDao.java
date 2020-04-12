package com.westee.wxshop.dao;

import com.westee.wxshop.entity.DataStatus;
import com.westee.wxshop.generate.Goods;
import com.westee.wxshop.generate.GoodsMapper;
import com.westee.wxshop.generate.UserMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GoodsDao {
    private final SqlSessionFactory sqlSessionFactory;
    private final GoodsMapper goodsMapper;

    @Autowired
    public GoodsDao(SqlSessionFactory sqlSessionFactory, GoodsMapper goodsMapper) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.goodsMapper = goodsMapper;
    }


    public Goods insertGoods(Goods goods) {
            long id = goodsMapper.insert(goods);
            goods.setId(id);
            return goods;
    }

    public Goods deleteGoodsById(Long goodsId) {
        Goods goods = goodsMapper.selectByPrimaryKey(goodsId);

        if(goods == null){
            throw new ResourceNotFoundException("商品未找到");
        }

        goods.setStatus(DataStatus.DELETE_STATUS);
        goodsMapper.updateByPrimaryKey(goods);
        return goods;
    }

    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }
}