package com.hbxy.gmall.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SkuLsResult implements Serializable {

    //商品集合
    List<SkuLsInfo> skuLsInfoList;

    //总条数
    long total;

    //总页数
    long totalPages;

    //平台属性值Id集合
    List<String> attrValueIdList;
}


