package com.westee.api.data;

import java.io.Serializable;
import java.util.List;

public class OrderInfo implements Serializable {
    List<GoodsInfo> goods;

    public List<GoodsInfo> getGoods() {
        return goods;
    }

    public void setGoods(List<GoodsInfo> goods) {
        this.goods = goods;
    }
}
