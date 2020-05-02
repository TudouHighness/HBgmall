package com.hbxy.gmall.order.mq;

import com.alibaba.dubbo.config.annotation.Reference;
import com.hbxy.gmall.bean.enums.ProcessStatus;
import com.hbxy.gmall.service.OrderService;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;

// 消费消息
@Component
public class OrderConsumer {

    @Reference
    private OrderService orderService;

    // 利用注解来获取消息的监听工厂
    @JmsListener(destination = "PAYMENT_RESULT_QUEUE",containerFactory = "jmsQueueListener")
    public void consumerPaymentResult(MapMessage mapMessage) throws JMSException {
        // 获取消息
        String result = mapMessage.getString("result");
        String orderId = mapMessage.getString("orderId");

        if ("success".equals(result)){
            // 支付成功！修改订单状态！
            // update orderInfo set ProcessStatus = Paid ,orderStatus = paid where id = orderId
            orderService.updateOrderStatus(orderId, ProcessStatus.PAID);
        }

    }
}