package com.westee.wxshop.service;

import com.westee.wxshop.controller.AuthController;
import com.westee.wxshop.entity.DataStatus;
import com.westee.wxshop.entity.HttpException;
import com.westee.wxshop.entity.PageResponse;
import com.westee.wxshop.generate.Shop;
import com.westee.wxshop.generate.ShopExample;
import com.westee.wxshop.generate.ShopMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
public class CheckTelService {
    private static Pattern TEL_PATTERN = Pattern.compile("1\\d{10}");
    /**
     * 验证参数是否合法
     *
     * @param param 输入的参数
     * @return true == 合法；false == 不合法
     */
    public boolean verifyTelParams(AuthController.TelAndCode param) {
        if (param == null) {
            return false;
        } else if (param.getTel() == null) {
            return false;
        } else {
            return  TEL_PATTERN.matcher(param.getTel()).find();
        }
    }

    @Service
    public static class ShopService {
        private ShopMapper shopMapper;

        @Autowired
        public ShopService(ShopMapper shopMapper) {
            this.shopMapper = shopMapper;
        }

        public PageResponse<Shop> getShopByUserId(Long userId, int pageNum, int pageSize) {
            ShopExample countByStatus = new ShopExample();
            countByStatus.createCriteria().andStatusEqualTo(DataStatus.DELETED.getName());
            int totalNumber = (int) shopMapper.countByExample(countByStatus);
            int totalPage = totalNumber % pageSize == 0 ? totalNumber / pageSize : totalNumber / pageSize + 1;

            ShopExample pageCondition = new ShopExample();
            pageCondition.createCriteria().andStatusEqualTo(DataStatus.OK.getName());
            pageCondition.setLimit(pageSize);
            pageCondition.setOffset((pageNum - 1) * pageSize);

            List<Shop> pagedShops = shopMapper.selectByExample(pageCondition);
            return PageResponse.pageData(pageNum, pageSize, totalPage, pagedShops);
        }

        public Shop createShop(Shop shop, Long creatorId) {
            shop.setOwnerUserId(creatorId);

            shop.setCreatedAt(new Date());
            shop.setUpdatedAt(new Date());
            shop.setStatus(DataStatus.OK.getName());
            long shopId = shopMapper.insert(shop);
            shop.setId(shopId);
            return shop;
        }

        public Shop updateShop(Shop shop, Long userId) {
            Shop shopInDatabase = shopMapper.selectByPrimaryKey(shop.getId());
            if (shopInDatabase == null) {
                throw HttpException.notFound("店铺未找到！");
            }

            if (!Objects.equals(shopInDatabase.getOwnerUserId(), userId)) {
                throw HttpException.forbidden("无权访问！");
            }

            shop.setUpdatedAt(new Date());
            shopMapper.updateByPrimaryKey(shop);
            return shop;
        }

        public Shop deleteShop(Long shopId, Long userId) {
            Shop shopInDatabase = shopMapper.selectByPrimaryKey(shopId);
            if (shopInDatabase == null) {
                throw HttpException.notFound("店铺未找到！");
            }

            if (!Objects.equals(shopInDatabase.getOwnerUserId(), userId)) {
                throw HttpException.forbidden("无权访问！");
            }

            shopInDatabase.setStatus(DataStatus.DELETED.getName());
            shopInDatabase.setUpdatedAt(new Date());
            shopMapper.updateByPrimaryKey(shopInDatabase);
            return shopInDatabase;
        }
    }
}
