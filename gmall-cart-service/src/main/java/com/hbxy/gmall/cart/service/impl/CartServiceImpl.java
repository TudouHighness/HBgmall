package com.hbxy.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.hbxy.gmall.bean.CartInfo;
import com.hbxy.gmall.bean.SkuInfo;
import com.hbxy.gmall.cart.constant.CartConst;
import com.hbxy.gmall.cart.mapper.CartInfoMapper;
import com.hbxy.gmall.config.RedisUtil;
import com.hbxy.gmall.service.CartService;
import com.hbxy.gmall.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartInfoMapper cartInfoMapper;

    @Reference
    private ManageService manageService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void addToCart(String skuId, String userId, Integer skuNum) {
        /*
        1.  判断购物车中是否又要添加的该商品
            true: 数量相加 mysql --
            false: 直接添加数据 mysql --
        2.  添加完成之后，必须更新redis
         */
        //获取redis
        Jedis jedis = redisUtil.getJedis();
        //数据类型
        String cartKey = CartConst.USER_KEY_PREFIX + userId +CartConst.USER_CART_KEY_SUFFIX;


        //select * from cartInfo where skuId = ? and userId = ?
//        CartInfo cartInfo = new CartInfo();
//        cartInfo.setUserId(userId);
//        cartInfo.setSkuId(skuId);
//        CartInfo cartInfoExist = cartInfoMapper.selectOne(cartInfo);
        Example example = new Example(CartInfo.class);
        example.createCriteria().andEqualTo("userId",userId).andEqualTo("skuId",skuId);
        CartInfo cartInfoExist = cartInfoMapper.selectOneByExample(example);

        if (cartInfoExist!=null){
            //购物车中有商品，数量相加
            cartInfoExist.setSkuNum(cartInfoExist.getSkuNum()+skuNum);
            //初始化SkuPrice 防止空指针
            cartInfoExist.setSkuPrice(cartInfoExist.getCartPrice());
            //更新数据库
            cartInfoMapper.updateByPrimaryKeySelective(cartInfoExist);
            //redis
            //jedis.hset(cartKey,skuId, JSON.toJSONString(cartInfoExist));

        }else{
            //添加数据来源
            SkuInfo skuInfo = manageService.getSkuInfo(skuId);
            CartInfo cartInfo1 = new CartInfo();

            cartInfo1.setSkuPrice(skuInfo.getPrice());
            cartInfo1.setCartPrice(skuInfo.getPrice());
            cartInfo1.setSkuNum(skuNum);
            cartInfo1.setSkuId(skuId);
            cartInfo1.setUserId(userId);
            cartInfo1.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo1.setSkuName(skuInfo.getSkuName());

            cartInfoMapper.insertSelective(cartInfo1);

            //redis
           // jedis.hset(cartKey,skuId, JSON.toJSONString(cartInfo1));
            cartInfoExist=cartInfo1;
        }
        //redis
        jedis.hset(cartKey,skuId, JSON.toJSONString(cartInfoExist));
        //设置过期时间
        setCartkeyExpire(userId, jedis, cartKey);
        jedis.close();
    }

    private void setCartkeyExpire(String userId, Jedis jedis, String cartKey) {
        //设置过期时间
        //获取用户的key
        String userKey = CartConst.USER_KEY_PREFIX+userId+CartConst.USERINFOKEY_SUFFIX;
        if (jedis.exists(userKey)){
            //获取用户Key的过期时间
            Long ttl = jedis.ttl(userKey);

            //将用户的过期时间给购物车的过期时间
            jedis.expire(cartKey, ttl.intValue());
        }else {
            jedis.expire(cartKey,7*24*3600);
        }
    }
}
