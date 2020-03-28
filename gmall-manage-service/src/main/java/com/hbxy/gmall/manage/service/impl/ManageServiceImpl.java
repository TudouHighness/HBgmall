package com.hbxy.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.hbxy.gmall.bean.*;
import com.hbxy.gmall.manage.mapper.*;
import com.hbxy.gmall.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
}
