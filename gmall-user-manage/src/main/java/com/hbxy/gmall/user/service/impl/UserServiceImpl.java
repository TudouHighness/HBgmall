package com.hbxy.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.hbxy.gmall.bean.UserAddress;
import com.hbxy.gmall.bean.UserInfo;
import com.hbxy.gmall.service.UserService;
import com.hbxy.gmall.user.mapper.UserAddressMapper;
import com.hbxy.gmall.user.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired(required = false)
    private UserInfoMapper userInfoMapper;

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Override
    public List<UserInfo> findAll() {
        return userInfoMapper.selectAll();
    }

    @Override
    public List<UserAddress> findUserAddressByUserId(String userId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        return userAddressMapper.select(userAddress);
    }
}
