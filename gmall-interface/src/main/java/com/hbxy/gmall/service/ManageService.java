package com.hbxy.gmall.service;

//后台管理

import com.hbxy.gmall.bean.*;

import java.util.List;

public interface ManageService {
    /**
     * 查询一级分类的所有数据
     * @return
     */
    List<BaseCatalog1> getCatalog1();

    /**
     * 通过一级分类id查询2级分类名称
     * @param catalog1Id
     * @return
     */
    List<BaseCatalog2> getCatalog2(String catalog1Id);

    /**
     * 通过二级分类对象查询数据
     * @param baseCatalog2
     * @return
     */
    List<BaseCatalog2> getCatalog2(BaseCatalog2 baseCatalog2);

    /**
     * 通过三级分类对象查询数据
     * @param baseCatalog3
     * @return
     */
    List<BaseCatalog3> getCatalog3(BaseCatalog3 baseCatalog3);

    /**
     * 通过三级分类id查询
     * @param baseAttrInfo
     * @return
     */
    List<BaseAttrInfo> getAttrInfoList(BaseAttrInfo baseAttrInfo);

    /**
     * 保存平台属性以及平台属性值
     * @param baseAttrInfo
     */
    void savaAttrInfo(BaseAttrInfo baseAttrInfo);

    /**
     * 根据attrId回显数据
     * @param attrId
     * @return
     */
    List<BaseAttrValue> getAttrValueList(String attrId);

    /**
     *通过attrId查询BaseAttrInfo
     * @param attrId
     * @return
     */
    BaseAttrInfo getBaseAttrInfo(String attrId);

    /**
     * 通过3级分类Id查询
     * @param catalog3Id
     * @return
     */
    List<SpuInfo> getSpuInfoList(String catalog3Id);

    /**
     * 获取销售属性值
     * @return
     */
    List<BaseSaleAttr> getBaseSaleAttrList();

    /**
     * 保存spuInfo
     * @param spuInfo
     */
    void saveSpuInfo(SpuInfo spuInfo);
}
