package com.hbxy.gmall.payment.mq;


import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.*;

public class ProducerTest {
    public static void main(String[] args) throws JMSException {
        // 创建连接工厂
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://172.17.152.219:61616");
        Connection connection = connectionFactory.createConnection();
        connection.start();
        // 创建session 第一个参数表示是否支持事务，false时，第二个参数Session.AUTO_ACKNOWLEDGE，Session.CLIENT_ACKNOWLEDGE，DUPS_OK_ACKNOWLEDGE其中一个
        // 第一个参数设置为true时，第二个参数可以忽略 服务器设置为SESSION_TRANSACTED
        Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
        // 创建队列
        Queue queue = session.createQueue("HBXY");

        MessageProducer producer = session.createProducer(queue);
        // 创建消息对象
        ActiveMQTextMessage activeMQTextMessage = new ActiveMQTextMessage();
        activeMQTextMessage.setText("hello ActiveMq111!");
        //开启事务必须先提交
        session.commit();
        // 发送消息
        producer.send(activeMQTextMessage);
        producer.close();
        connection.close();
    }
}
