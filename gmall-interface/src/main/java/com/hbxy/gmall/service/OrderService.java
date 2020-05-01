package com.hbxy.gmall.service;

import com.hbxy.gmall.bean.OrderInfo;

public interface OrderService {
    /**
     * 保存订单信息 返回订单编号
     * @param orderInfo
     * @return
     */
    String saveOrderInfo(OrderInfo orderInfo);

    /**
     * 生成流水号
     * @param userId
     * @return
     */
    String getTradeNo(String userId);

    /**
     * 验证流水号
     * @param userId
     * @param tradeNo
     * @return
     */
    boolean checkTradeNo(String tradeNo, String userId);

    /**
     * 删除流水
     * @param userId
     */
    void delTradeNo(String userId);

    boolean checkStock(String skuId, Integer skuNum);

    OrderInfo getOrderInfo(String orderId);
}