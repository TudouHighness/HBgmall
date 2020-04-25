package com.hbxy.gmall.service;

import com.hbxy.gmall.bean.SkuLsInfo;
import com.hbxy.gmall.bean.SkuLsParams;
import com.hbxy.gmall.bean.SkuLsResult;

public interface ListService {

    /**
     * 商品上架
     * @param skuLsInfo
     */
    public void saveSkuInfo(SkuLsInfo skuLsInfo);

    SkuLsResult search(SkuLsParams skuLsParams);

    public void incrHotScore(String skuId);

}
