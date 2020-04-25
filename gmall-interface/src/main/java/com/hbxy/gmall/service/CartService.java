package com.hbxy.gmall.service;

public interface CartService {

    /**
     * 添加购物车
     * @param skuId
     * @param userId
     * @param skuNum
     */
    void  addToCart(String skuId,String userId,Integer skuNum);
}
