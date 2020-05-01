package com.hbxy.gmall.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.hbxy.gmall.bean.OrderDetail;
import com.hbxy.gmall.bean.OrderInfo;
import com.hbxy.gmall.bean.enums.OrderStatus;
import com.hbxy.gmall.bean.enums.ProcessStatus;
import com.hbxy.gmall.config.RedisUtil;
import com.hbxy.gmall.order.mapper.OrderDetailMapper;
import com.hbxy.gmall.order.mapper.OrderInfoMapper;
import com.hbxy.gmall.service.OrderService;
import com.hbxy.gmall.util.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    @Transactional
    public String saveOrderInfo(OrderInfo orderInfo) {
        // 订单的总金额，订单的状态，用户Id，第三方交易编号，创建时间，过期时间，进程状态
        //总金额
        orderInfo.sumTotalAmount();
        orderInfo.setOrderStatus(OrderStatus.UNPAID);

        //订单创建时间
        orderInfo.setCreateTime(new Date());

        //设置过期时间
        //获取当前时间
        Calendar calendar = Calendar.getInstance();
        //当前时间加1天
        calendar.add(Calendar.DATE,1);
        orderInfo.setExpireTime(calendar.getTime());


        //当单状态  未支付
        orderInfo.setProcessStatus(ProcessStatus.UNPAID);
        // orderInfo
        orderInfoMapper.insertSelective(orderInfo);

        // orderDetail
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        // 循环遍历
        for (OrderDetail orderDetail : orderDetailList) {
            orderDetail.setId(null);
            orderDetail.setOrderId(orderInfo.getId());
            orderDetailMapper.insertSelective(orderDetail);
        }
        // 返回订单编号
        return orderInfo.getId();
    }

    @Override
    public String getTradeNo(String userId) {
        //获取jedis
        Jedis jedis = redisUtil.getJedis();

        //确定数据类型
        String tradeNoKey="user:"+userId+":tradeCode";
        String tradeCode = UUID.randomUUID().toString();
        jedis.setex(tradeNoKey,10*60,tradeCode);
        jedis.close();
        return tradeCode;

    }

    @Override
    public boolean checkTradeNo(String tradeNo, String userId) {
        // 获取缓存的流水号
        // 获取jedis
        Jedis jedis = redisUtil.getJedis();

        // 确定数据类型。String
        String tradeNoKey ="user:"+userId+":tradeCode";

        String tradeCode = jedis.get(tradeNoKey);

        jedis.close();
        return tradeNo.equals(tradeCode);
    }

    @Override
    public void delTradeNo(String userId) {
        // 获取jedis
        Jedis jedis = redisUtil.getJedis();

        // 确定数据类型。String
        String tradeNoKey ="user:"+userId+":tradeCode";

        jedis.del(tradeNoKey);

        jedis.close();
    }

    @Override
    public boolean checkStock(String skuId, Integer skuNum) {
        // 远程调用库存接口方法 http://www.gware.com/hasStock?skuId=10221&num=2
        String result = HttpClientUtil.doGet("http://www.gware.com/hasStock?skuId=" + skuId + "&num=" + skuNum);
        return "1".equals(result);
    }

    @Override
    public OrderInfo getOrderInfo(String orderId) {
        return orderInfoMapper.selectByPrimaryKey(orderId);
    }
}
