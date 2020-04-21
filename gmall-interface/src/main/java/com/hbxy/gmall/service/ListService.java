package com.hbxy.gmall.service;

import com.hbxy.gmall.bean.SkuLsInfo;

public interface ListService {

    /**
     * 商品上架
     * @param skuLsInfo
     */
    public void saveSkuInfo(SkuLsInfo skuLsInfo);
}
