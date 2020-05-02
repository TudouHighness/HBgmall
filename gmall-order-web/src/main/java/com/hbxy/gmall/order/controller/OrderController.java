package com.hbxy.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.hbxy.gmall.bean.*;
import com.hbxy.gmall.bean.enums.OrderStatus;
import com.hbxy.gmall.config.LoginRequire;
import com.hbxy.gmall.service.CartService;
import com.hbxy.gmall.service.ManageService;
import com.hbxy.gmall.service.OrderService;
import com.hbxy.gmall.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
public class OrderController {
    @Reference
    private UserService userService;

    @Reference
    private CartService cartService;

    @Reference
    private OrderService orderService;

    @Reference
    private ManageService manageService;

    //根据用户Id查询收获地址列表
    @RequestMapping("trade")
    @LoginRequire(autoRedirect = true)
    public String trade(HttpServletRequest request){
        //获取用户Id
        String userId = (String) request.getAttribute("userId");
        List<UserAddress> userAddressesList = userService.findUserAddressByUserId(userId);

        //获取数据库中选中的数据
        List<CartInfo> cartInfoList = cartService.getCartCheckedList(userId);

        //存储订单明细
        ArrayList<OrderDetail> detailArrayList = new ArrayList<>();

        for (CartInfo cartInfo : cartInfoList){
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setSkuId(cartInfo.getSkuId());
            orderDetail.setSkuName(cartInfo.getSkuName());
            orderDetail.setImgUrl(cartInfo.getImgUrl());
            orderDetail.setSkuNum(cartInfo.getSkuNum());
            orderDetail.setOrderPrice(cartInfo.getCartPrice());
            //添加订单明细
            detailArrayList.add(orderDetail);

        }
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderDetailList(detailArrayList);
        // 计算总金额
        orderInfo.sumTotalAmount();
        // 保存数据
        request.setAttribute("detailArrayList",detailArrayList);
        request.setAttribute("totalAmount",orderInfo.getTotalAmount());
        request.setAttribute("userAddressesList",userAddressesList);

        //流水号发送前端
        String tradeNo = orderService.getTradeNo(userId);
        request.setAttribute("tradeNo",tradeNo);
        return"trade";
    }


    //http://trade.gmall.com/submitOrder
    @RequestMapping("submitOrder")
    @LoginRequire(autoRedirect = true)
    public String submitOrder(OrderInfo orderInfo,HttpServletRequest request){
        //将订单信息存入数据库
        // 调用服务层！保存
        // 订单的总金额，订单的状态，用户Id，第三方交易编号，创建时间，过期时间，进程状态


        String userId = (String) request.getAttribute("userId");
        orderInfo.setUserId(userId);

        String outTradeNo="HBXY"+System.currentTimeMillis()+""+new Random().nextInt(1000);
        orderInfo.setOutTradeNo(outTradeNo);


        //防止表单重复提交
        String tradeNo = request.getParameter("tradeNo");
        // 调用比较方法
        boolean result = orderService.checkTradeNo(tradeNo, userId);
        // 验证失败！
        if (!result){
            request.setAttribute("errMsg","请勿重复提交订单！");
            return "tradeFail";
        }

        // 删除缓存的流水号
        orderService.delTradeNo(userId);

        // 验证库存！
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetailList) {
            boolean flag = orderService.checkStock(orderDetail.getSkuId(), orderDetail.getSkuNum());
            if (!flag){
                request.setAttribute("errMsg",orderDetail.getSkuName()+"库存不足，请联系客服！");
                return "tradeFail";
            }
            // 验证价格：orderDetail.getOrderPrice()== skuInfo.price
            SkuInfo skuInfo = manageService.getSkuInfo(orderDetail.getSkuId());
            //
            int res = orderDetail.getOrderPrice().compareTo(skuInfo.getPrice());
            if (res!=0){
                request.setAttribute("errMsg",orderDetail.getSkuName()+"商品价格有变动，请重新下单！");
                // 加载最新价格到缓存！
                cartService.loadCartCache(userId);
                return "tradeFail";
            }
        }

        String orderId = orderService.saveOrderInfo(orderInfo);

     return "redirect://payment.gmall.com/index?orderId="+orderId;
    }

    //根据用户Id查询收获地址列表
    @RequestMapping("tradeSuccess")
    public String tradeSuccess(){
        return"tradeSuccess";
    }
}
