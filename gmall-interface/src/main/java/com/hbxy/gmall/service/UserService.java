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

    /**
     * 登录
     * @param userInfo
     * @return
     */
    UserInfo login(UserInfo userInfo);

    /**
     * 解密token获取用户id
     * @param userId
     * @return
     */
    UserInfo verfiy(String userId);

    /**
     * 退出登录
     * @param key
     */
    void signOut(String key);
}
