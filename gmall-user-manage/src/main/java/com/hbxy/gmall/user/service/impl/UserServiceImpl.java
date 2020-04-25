package com.hbxy.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;

import com.hbxy.gmall.bean.UserAddress;
import com.hbxy.gmall.bean.UserInfo;
import com.hbxy.gmall.config.RedisUtil;
import com.hbxy.gmall.service.UserService;
import com.hbxy.gmall.user.mapper.UserAddressMapper;
import com.hbxy.gmall.user.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired(required = false)
    private UserInfoMapper userInfoMapper;

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Autowired
    private RedisUtil redisUtil;

    public String userKey_prefix="user:";
    public String userinfoKey_suffix=":info";
    public int userKey_timeOut=60*60*24*7;


    @Override
    public List<UserInfo> findAll() {
        return userInfoMapper.selectAll();
    }

    @Override
    public List<UserAddress> findUserAddressByUserId(String userId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        return userAddressMapper.select(userAddress);
        // return userAddressMapper.select(new UserAddress().setUserId(userId));
    }

    @Override
    public UserInfo login(UserInfo userInfo) {
        // select * from userInfo where loginName = ? and passwd = ?
        // 要对密码进行加密
        String passwd = userInfo.getPasswd(); // 123
        // passwd md5
        String newPasswd = DigestUtils.md5DigestAsHex(passwd.getBytes());
        userInfo.setPasswd(newPasswd);
        UserInfo info = userInfoMapper.selectOne(userInfo);

        if (info!=null){
            // 获取jedis
            Jedis jedis = redisUtil.getJedis();
            // 确定数据类型 String
            // 定义key user:userId:info
            String userKey = userKey_prefix + info.getId() + userinfoKey_suffix;
            // 设置用户的过期时间
            jedis.setex(userKey,userKey_timeOut, JSON.toJSONString(info));

            jedis.close();
            return info;
        }
        return null;
    }

    @Override
    public UserInfo verfiy(String userId) {
        // 获取jedis
        Jedis jedis = redisUtil.getJedis();
        // 组成userKey
        String userKey = userKey_prefix+userId+userinfoKey_suffix;
        // 获取缓存中的数据
        String userJson = jedis.get(userKey);
        if (!StringUtils.isEmpty(userJson)){
            // userJson 转换成对象
            UserInfo userInfo = JSON.parseObject(userJson, UserInfo.class);
            return userInfo;
        }

        return null;
    }

    @Override
    public void signOut(String key) {
        String redisKey = "user:"+key+":info";
        Jedis jedis = redisUtil.getJedis();
        jedis.del(redisKey);
        jedis.close();
    }
}
