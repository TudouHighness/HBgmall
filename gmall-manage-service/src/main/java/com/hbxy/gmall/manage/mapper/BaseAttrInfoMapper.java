package com.hbxy.gmall.manage.mapper;

import com.hbxy.gmall.bean.BaseAttrInfo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseAttrInfoMapper extends Mapper<BaseAttrInfo> {
    //通过三级分类id查询
    List<BaseAttrInfo> selectBaseAttrInfoListByCatalog3Id(String catalog3Id);
}
