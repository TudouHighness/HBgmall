package com.hbxy.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.hbxy.gmall.bean.UserAddress;
import com.hbxy.gmall.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OrderController {
    @Reference
    private UserService userService;

    //根据用户Id查询收获地址列表
 @RequestMapping("trade")
    public List<UserAddress> trade(String userId){
        return userService.findUserAddressByUserId(userId);
    }
}
