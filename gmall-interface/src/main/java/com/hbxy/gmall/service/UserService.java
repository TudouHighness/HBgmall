package com.hbxy.gmall.service;

import com.hbxy.gmall.bean.UserAddress;
import com.hbxy.gmall.bean.UserInfo;

import java.util.List;

//业务层接口
public interface UserService {

    /**
     * 查询所有数据
     * @return
     */
    List<UserInfo> findAll();

    /**
     * 根据用户ID查询用户地址列表
     * @return
     */
    List<UserAddress> findUserAddressByUserId(String userId);
}
