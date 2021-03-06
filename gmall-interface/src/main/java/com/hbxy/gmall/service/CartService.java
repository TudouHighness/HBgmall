package com.hbxy.gmall.service;

import com.hbxy.gmall.bean.CartInfo;

import java.util.List;

public interface CartService {

    /**
     * 添加购物车
     * @param skuId
     * @param userId
     * @param skuNum
     */
    void  addToCart(String skuId,String userId,Integer skuNum);

    /**
     * 获取购物车列表
     * @param userId
     * @return
     */
    List<CartInfo> getCartList(String userId);

    /**
     *合并
     * @param cartInfoNoLoginList
     * @param userId
     * @return
     */
    List<CartInfo> mergeToCartList(List<CartInfo> cartInfoNoLoginList, String userId);

    /**
     *shan除未登录购物车
     * @param userTempId
     */
    void deleteCartList(String userTempId);

    /**
     *修改购物车zhuangtai
     * @param skuId
     * @param userId
     * @param isCheck
     */
    void checkCart(String skuId, String userId, String isCheck);

    /**
     * 根据用户id查询购物车列表
     * @param userId
     * @return
     */
    List<CartInfo> getCartCheckedList(String userId);

    /**
     * 根据用户id查询数据库并放入缓存
     * @param userId
     * @return
     */
    List<CartInfo> loadCartCache(String userId);

    /**
     * 删除已买
     * @param userTempId
     */
    void deleteBuyCartList(String userTempId);
}
