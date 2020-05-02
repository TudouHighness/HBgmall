package com.hbxy.gmall.service;

import com.hbxy.gmall.bean.PaymentInfo;

public interface PaymentService {

    /**
     * 保存记录并返回二维码
     * @param paymentInfo
     */
    void  savePaymentInfo(PaymentInfo paymentInfo);

    /**
     * 根据OrderTradeNo查询数据
     * @param paymentInfo
     * @return
     */
    PaymentInfo getPaymentInfo(PaymentInfo paymentInfo);

    /**
     * 更新交易记录中的状态
     * @param out_trade_no
     * @param paymentInfoUpd
     */
    void updatePaymentInfo(String out_trade_no, PaymentInfo paymentInfoUpd);

    /**
     * 根据订单Id 退款
     * @param orderId
     * @return
     */
    boolean refund(String orderId);
}
