package com.hbxy.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.hbxy.gmall.bean.*;
import com.hbxy.gmall.config.RedisUtil;
import com.hbxy.gmall.manage.constant.ManageConst;
import com.hbxy.gmall.manage.mapper.*;
import com.hbxy.gmall.service.ManageService;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class ManageServiceImpl implements ManageService {

    //调用mapper
    @Autowired
    private BaseCatalog1Mapper baseCatalog1Mapper;

    @Autowired
    private BaseCatalog2Mapper baseCatalog2Mapper;

    @Autowired
    private BaseCatalog3Mapper baseCatalog3Mapper;

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;

    @Autowired//默认type
    private SpuInfoMapper spuInfoMapper;

    @Resource//默认按照name  如果没有name 找type
    private BaseSaleAttrMapper baseSaleAttrMapper;

    @Autowired
    private SpuImageMapper spuImageMapper;

    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    private SkuImageMapper skuImageMapper;

    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<BaseCatalog1> getCatalog1() {
        return baseCatalog1Mapper.selectAll();
    }

    @Override
    public List<BaseCatalog2> getCatalog2(String catalog1Id) {
    BaseCatalog2 baseCatalog2 = new BaseCatalog2();
    baseCatalog2.setCatalog1Id(catalog1Id);
    return baseCatalog2Mapper.select(baseCatalog2);
    }

    @Override
    public List<BaseCatalog2> getCatalog2(BaseCatalog2 baseCatalog2) {
        return baseCatalog2Mapper.select(baseCatalog2);
    }

    @Override
    public List<BaseCatalog3> getCatalog3(BaseCatalog3 baseCatalog3) {
        return baseCatalog3Mapper.select(baseCatalog3);
    }

    @Override
    public List<BaseAttrInfo> getAttrInfoList(BaseAttrInfo baseAttrInfo) {
        //通用mapper对单张表curd
        return baseAttrInfoMapper.select(baseAttrInfo);
    }

    @Override
    @Transactional
    //不仅保存 还有修改功能
    public void savaAttrInfo(BaseAttrInfo baseAttrInfo) {
        //保存baseAttrInfo   baseAttrValue  两张表
        //保存/修改baseAttrInfo
        if (baseAttrInfo.getId()!=null && baseAttrInfo.getId().length()>0){
            //修改
            baseAttrInfoMapper.updateByPrimaryKeySelective(baseAttrInfo);

        }else{
            //直接保存平台属性
            baseAttrInfoMapper.insertSelective(baseAttrInfo);
        }
        //保存/修改baseAttrValue
        //修改，先删除原有数据
        BaseAttrValue baseAttrValueDel = new BaseAttrValue();
        baseAttrValueDel.setAttrId(baseAttrInfo.getId());
        baseAttrValueMapper.delete(baseAttrValueDel);

        //再保存
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        //判断集合是否为空
        //先判断对象不为空，在判断集合长度
        if (attrValueList != null && attrValueList.size()>0){
            for (BaseAttrValue baseAttrValue : attrValueList) {
                //平台属性值Id主键自增，平台属性Id baseAttrValue.attrId = baseAttrInfo.id
                baseAttrValue.setAttrId(baseAttrInfo.getId());//获取当前对象主键自增值
                baseAttrValueMapper.insertSelective(baseAttrValue);
            }
        }


    }

    @Override
    public List<BaseAttrValue> getAttrValueList(String attrId) {
        BaseAttrValue baseAttrValue= new BaseAttrValue();
        baseAttrValue.setAttrId(attrId);
        return baseAttrValueMapper.select(baseAttrValue);
    }

    @Override
    public BaseAttrInfo getBaseAttrInfo(String attrId) {
        BaseAttrInfo baseAttrInfo = baseAttrInfoMapper.selectByPrimaryKey(attrId);
        //查询平台属性值集合
        baseAttrInfo.setAttrValueList(getAttrValueList(attrId));
        return baseAttrInfo;
    }

    @Override
    public List<SpuInfo> getSpuInfoList(String catalog3Id) {
        //select * from spu_info where catalog3Id = 62
        SpuInfo spuInfo = new SpuInfo();
        spuInfo.setCatalog3Id(catalog3Id);
        return spuInfoMapper.select(spuInfo);
    }

    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        return baseSaleAttrMapper.selectAll();
    }

    @Override
    @Transactional
    public void saveSpuInfo(SpuInfo spuInfo) {
        //spuinfo  spuimage  spuSaleAttr  spuSaleAttrValue
        spuInfoMapper.insertSelective(spuInfo);
        //spuImage
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        if (spuImageList != null &&spuImageList.size() > 0){
            for (SpuImage spuImage : spuImageList){
                //赋值spuid
                spuImage.setSpuId(spuInfo.getId());
                spuImageMapper.insertSelective(spuImage);
            }
        }
        //销售属性
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if (spuSaleAttrList != null && spuSaleAttrList.size() > 0){
            for (SpuSaleAttr spuSaleAttr : spuSaleAttrList){
                spuSaleAttr.setSpuId(spuInfo.getId());
                spuSaleAttrMapper.insertSelective(spuSaleAttr);

                //销售属性值
                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                if (spuSaleAttrValueList != null && spuSaleAttrList.size() > 0){
                    for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList){
                        spuSaleAttrValue.setSpuId(spuInfo.getId());
                        spuSaleAttrValueMapper.insertSelective(spuSaleAttrValue);
                    }
                }
            }
        }


    }

    @Override
    public List<SpuImage> getSpuImageList(SpuImage spuImage) {

        return spuImageMapper.select(spuImage);
    }

    @Override
    public List<BaseAttrInfo> getAttrInfoList(String catalog3Id) {
        //SELECT * FROM base_attr_info bai INNER  JOIN base_attr_value bav ON bai.id = bav.attr_id WHERE bai.catalog3_id=61;
        return baseAttrInfoMapper.selectBaseAttrInfoListByCatalog3Id(catalog3Id);
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(String spuId) {
        return spuSaleAttrMapper.selectSpuSaleAttrList(spuId);
    }

    @Override
    @Transactional
    public void saveSkuInfo(SkuInfo skuInfo) {
//         保存四张表
//         SkuInfo
        skuInfoMapper.insertSelective(skuInfo);
//         skuAttrValue
        //获取sku与平台属性关系
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if (skuAttrValueList != null&&skuAttrValueList.size()>0){
            for (SkuAttrValue skuAttrValue :skuAttrValueList){
                skuAttrValue.setSkuId(skuInfo.getId());
                skuAttrValueMapper.insertSelective(skuAttrValue);
            }
        }
//         skuSaleAttrValue
        //获取sku与销售属性集合
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        if (skuSaleAttrValueList != null&&skuSaleAttrValueList.size()>0){
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList){
                //skuId赋值
                skuSaleAttrValue.setSkuId(skuInfo.getId());
                skuSaleAttrValueMapper.insertSelective(skuSaleAttrValue);
            }
        }
//         skuImage
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        if (skuImageList != null && skuImageList.size()>0){
            for (SkuImage skuImage:skuImageList){
                skuImage.setSkuId(skuInfo.getId());
                skuImageMapper.insertSelective(skuImage);
            }
        }

    }

    @Override
    public SkuInfo getSkuInfo(String skuId) {
        //测试是否能获取到redis
//        Jedis jedis = redisUtil.getJedis();
//        jedis.set("age","18");
//        jedis.close();
//
//        SkuInfo skuInfo = skuInfoMapper.selectByPrimaryKey(skuId);
//        SkuImage skuImage = new SkuImage();
//        skuImage.setSkuId(skuId);
//        skuInfo.setSkuImageList(skuImageMapper.select(skuImage));
//        return skuInfo;
        return getSkuInfoRedisson(skuId);
    }

    private SkuInfo getSkuInfoRedisson(String skuId) {
        SkuInfo skuInfo = null;
        Jedis jedis = null;
        try {
            // 获取Jedis
            jedis = redisUtil.getJedis();
            // 定义key
            String skuKey = ManageConst.SKUKEY_PREFIX+skuId+ManageConst.SKUKEY_SUFFIX;

            String skuJson = jedis.get(skuKey);

            if(skuJson==null){
                // redisson 加锁，走数据库并放入缓存！
                Config config = new Config();
                // 设置redis 节点
                config.useSingleServer().setAddress("redis://172.17.152.219:6379");

                // 创建Redisson 实例
                RedissonClient redisson = Redisson.create(config);
                // 创建锁
                RLock lock = redisson.getLock("myLock");
                System.out.println("redisson 分布式锁！");
                // lock.lock();  set key,vlaue,px,10000,nx
                // lock.lock(10, TimeUnit.SECONDS);
                boolean res = lock.tryLock(100, 10, TimeUnit.SECONDS);
                if (res) {
                    try {
                        // 业务逻辑
                        // 缓存中没有数据
                        skuInfo =  getSkuInfoDB(skuId);
                        // 将数据放入缓存
                        jedis.setex(skuKey,ManageConst.SKUKEY_TIMEOUT, JSON.toJSONString(skuInfo));
                        return skuInfo;
                    } finally {
                        // 解锁！
                        lock.unlock();
                    }
                }
            }else {
                // 缓存有数据
                // skuJson -- 转换为对象
                skuInfo = JSON.parseObject(skuJson,SkuInfo.class);
                return skuInfo;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭缓存
            if (jedis!=null){
                jedis.close();
            }
        }
        return getSkuInfoDB(skuId);
    }

    private SkuInfo getSkuInfoByRedisSet(String skuId) {
        SkuInfo skuInfo = null;
        Jedis jedis = null;
        try {
            // 获取Jedis、
            jedis = redisUtil.getJedis();
            // 定义key
            String skuKey = ManageConst.SKUKEY_PREFIX+skuId+ManageConst.SKUKEY_SUFFIX;

            String skuJson = jedis.get(skuKey);
            if (skuJson==null){
                // 没有数据
                System.out.println("缓存中没有数据：");
                // 准备加锁！  set k1 v1 px 10000 nx
                // 定义锁的key sku:skuId:lock  k1
                String skuLockKey = ManageConst.SKUKEY_PREFIX+skuId+ManageConst.SKULOCK_SUFFIX;

                // 定义key 锁定的值  v1
                String token = UUID.randomUUID().toString().replace("-","");

                // 执行加锁命令
                String lockKey = jedis.set(skuLockKey, token, "NX", "PX", ManageConst.SKULOCK_EXPIRE_PX);
                if ("OK".equals(lockKey)){
                    System.out.println("上锁成功！");
                    // 从db 中数据并放入缓存！
                    // 缓存中没有数据
                    skuInfo =  getSkuInfoDB(skuId);
                    // 将数据放入缓存
                    jedis.setex(skuKey,ManageConst.SKUKEY_TIMEOUT, JSON.toJSONString(skuInfo));

                    // 解锁：
                    // jedis.del(skuKey); lua 脚本：
                    String script ="if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                    jedis.eval(script, Collections.singletonList(skuLockKey),Collections.singletonList(token));

                    return skuInfo;
                }else {
                    // 说明里面有人！等待
                    Thread.sleep(1000);

                    return getSkuInfo(skuId);
                }

            }else {
                // 缓存中有数据！
                // skuJson -- 转换为对象
                skuInfo = JSON.parseObject(skuJson,SkuInfo.class);
                return skuInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭缓存
            if (jedis!=null){
                jedis.close();
            }
        }

        return getSkuInfoDB(skuId);
    }

    // 抽取方法单独走数据库！
    private SkuInfo getSkuInfoDB(String skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectByPrimaryKey(skuId);
        // select * from skuImage where skuId = skuId
        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuId);
        skuInfo.setSkuImageList( skuImageMapper.select(skuImage));
        return skuInfo;
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(SkuInfo skuInfo) {
        return spuSaleAttrMapper.selectSpuSaleAttrListCheckBySku(skuInfo.getId(),skuInfo.getSpuId());
    }

    @Override
    public List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String spuId) {
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuSaleAttrValueMapper.selectSkuSaleAttrValueListBySpu(spuId);
        return skuSaleAttrValueList;
    }
}
