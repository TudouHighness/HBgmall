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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

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

        if (!jedis.exists(cartKey)){
            // 加载数据到缓存！
            loadCartCache(userId);
        }

        //select * from cartInfo where skuId = ? and userId = ?
//        CartInfo cartInfo = new CartInfo();
//        cartInfo.setUserId(userId);
//        cartInfo.setSkuId(skuId);
//        CartInfo cartInfoExist = cartInfoMapper.selectOne(cartInfo);
        Example example = new Example(CartInfo.class);
        example.createCriteria().andEqualTo("userId",userId).andEqualTo("skuId",skuId);
        CartInfo cartInfoExist = cartInfoMapper.selectOneByExample(example);
        //查询商品信息
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);

        if (cartInfoExist!=null){
            //购物车中有商品，数量相加
            cartInfoExist.setSkuNum(cartInfoExist.getSkuNum()+skuNum);
            //初始化SkuPrice 防止空指针
//            cartInfoExist.setSkuPrice(cartInfoExist.getCartPrice());
            cartInfoExist.setSkuPrice(skuInfo.getPrice());
            //更新数据库
            cartInfoMapper.updateByPrimaryKeySelective(cartInfoExist);
            //redis
            //jedis.hset(cartKey,skuId, JSON.toJSONString(cartInfoExist));

        }else{
            //添加数据来源
//            SkuInfo skuInfo = manageService.getSkuInfo(skuId);
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

    @Override
    public List<CartInfo> getCartList(String userId) {
        /**
         * 先获取缓存中的数据
         *  true  直接查询并返回集合
         *  false 查询数据库 放入缓存
         *  查询实时价格
         */

        List<CartInfo> cartInfoList = new ArrayList<>();
        //获取redis
        Jedis jedis = redisUtil.getJedis();

        //数据类型
        String cartKey = CartConst.USER_KEY_PREFIX + userId +CartConst.USER_CART_KEY_SUFFIX;


        //hash获取数据
        List<String> stringList = jedis.hvals(cartKey);
        //遍历获取数据
        if (stringList!=null&&stringList.size()>0){
            for (String cartInfoJson : stringList){
//                将缓存中的cartInfo添加到集合
                cartInfoList.add(JSON.parseObject(cartInfoJson,CartInfo.class));
            }
            //自定义比较器
            cartInfoList.sort(new Comparator<CartInfo>() {
                @Override
                public int compare(CartInfo o1, CartInfo o2) {
                    return o1.getId().compareTo(o2.getId());
                }
            });
            return cartInfoList;
        }else{
            //走db放入缓存
            cartInfoList = loadCartCache(userId);
            return  cartInfoList;
        }

    }

    @Override
    public List<CartInfo> mergeToCartList(List<CartInfo> cartInfoNoLoginList, String userId) {
        //定义合并之后的
        List<CartInfo> cartInfoList = new ArrayList<>();
        //登录+未登录
        List<CartInfo> cartInfoLoginList = cartInfoMapper.selectCartListWithCurPrice(userId);
        //准备合并工作
        if (cartInfoLoginList!=null&&cartInfoLoginList.size()>0){
            //循环判断 skuid
            for (CartInfo cartInfoNoLogin : cartInfoNoLoginList) {
                //是否有相同商品
                boolean isMatch = false;
                for (CartInfo cartInfoLogin : cartInfoLoginList) {
                    if (cartInfoLogin.getSkuId().equals(cartInfoNoLogin.getSkuId())){
                        //数量相加
                        cartInfoLogin.setSkuNum(cartInfoLogin.getSkuNum()+cartInfoNoLogin.getSkuNum());
                        //更新到数据库
                        cartInfoMapper.updateByPrimaryKeySelective(cartInfoLogin);
                        isMatch=true;
                    }
                }
                //没有相同
                if (!isMatch){
                    //直接将未登录加入登录
                    cartInfoNoLogin.setId(null);
                    //设置登陆的userId给未登录的Id
                    cartInfoNoLogin.setUserId(userId);
                    cartInfoMapper.insertSelective(cartInfoNoLogin);
                }

            }

        }else{
            //登录无数据，直接使用未登录
            for (CartInfo cartInfo : cartInfoNoLoginList) {
                cartInfo.setId(userId);
                cartInfo.setUserId(userId);
                cartInfoMapper.insertSelective(cartInfo);
            }
        }
        //汇总
        cartInfoList =loadCartCache(userId);
        return cartInfoList;
    }

    @Override
    public void deleteCartList(String userTempId) {
        //删除未登录 redis+mysql

        //mysql
        Example example = new Example(CartInfo.class);
        example.createCriteria().andEqualTo("userId",userTempId);
        cartInfoMapper.deleteByExample(example);

        // 删除缓存
        Jedis jedis = redisUtil.getJedis();
        String cartKey = CartConst.USER_KEY_PREFIX+userTempId+CartConst.USER_CART_KEY_SUFFIX;
        jedis.del(cartKey);
        jedis.close();
    }

    //根据用户id查询数据库并放入缓存
    private List<CartInfo> loadCartCache(String userId) {
        //查询最新数据给缓存！价格商品
        List<CartInfo> cartInfoList = cartInfoMapper.selectCartListWithCurPrice(userId);
        if (cartInfoList==null||cartInfoList.size()==0){
            return null;
        }
        //获取redis
        Jedis jedis = redisUtil.getJedis();
        //数据类型
        String cartKey = CartConst.USER_KEY_PREFIX + userId +CartConst.USER_CART_KEY_SUFFIX;

        HashMap<String,String> map = new HashMap<>();
        //循环遍历数据 添加缓存
        for (CartInfo cartInfo :cartInfoList){
            map.put(cartInfo.getSkuId(), JSON.toJSONString(cartInfo));
        }
        jedis.hmset(cartKey,map);
        jedis.close();
        return cartInfoList;
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
