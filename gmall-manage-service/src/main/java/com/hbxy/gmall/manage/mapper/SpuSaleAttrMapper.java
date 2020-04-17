package com.hbxy.gmall.manage.mapper;

import com.hbxy.gmall.bean.SpuSaleAttr;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SpuSaleAttrMapper extends Mapper<SpuSaleAttr> {

    /**
     * 通过spuId查询销售属性集合
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> selectSpuSaleAttrList(String spuId);

    /**
     *
     * @param skuId
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> selectSpuSaleAttrListCheckBySku(String skuId, String spuId);
}

