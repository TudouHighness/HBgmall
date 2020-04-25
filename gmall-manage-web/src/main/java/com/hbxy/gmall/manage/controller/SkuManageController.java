package com.hbxy.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.hbxy.gmall.bean.*;
import com.hbxy.gmall.service.ListService;
import com.hbxy.gmall.service.ManageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class SkuManageController {

    @Reference
    private ManageService manageService;

    @Reference
    private ListService listService;


    //http://localhost:8082/spuSaleAttrList?spuId=63  回显平台属性以及属性值
    @RequestMapping("spuSaleAttrList")
    public List<SpuSaleAttr> getSpuSaleAttrList(String spuId){
        return manageService.getSpuSaleAttrList(spuId);
    }


    //http://localhost:8082/spuImageList?spuId=63
    @RequestMapping("spuImageList")
    public List<SpuImage> getSpuImageList(SpuImage spuImage){
        return manageService.getSpuImageList(spuImage);

    }

    //http://localhost:8082/saveSkuInfo
    @RequestMapping("saveSkuInfo")
    public void saveSkuInfo(@RequestBody SkuInfo skuInfo){
        manageService.saveSkuInfo(skuInfo);
        //保存完成后商品上架es
        //发送消息队列异步处理 通知管理员审核   成功后上架
        onSaleAuto(skuInfo.getId());

    }


    public void onSaleAuto(String skuId){
        //商品上架
        SkuLsInfo skuLsInfo = new SkuLsInfo();


        //给skuLsInfo初始化赋值
        //根据skuId查询skuInfo
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);
        skuInfo.setSkuAttrValueList( manageService.getSkuAttrValueList(skuId));
        //属性拷贝
        BeanUtils.copyProperties(skuInfo,skuLsInfo);
        listService.saveSkuInfo(skuLsInfo);
    }
    //根据SkuId上传
    @RequestMapping("onSale")
    public void onSale(String skuId){
        //商品上架
        SkuLsInfo skuLsInfo = new SkuLsInfo();


        //给skuLsInfo初始化赋值
        //根据skuId查询skuInfo
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);
        skuInfo.setSkuAttrValueList( manageService.getSkuAttrValueList(skuId));
        //属性拷贝
        BeanUtils.copyProperties(skuInfo,skuLsInfo);
        listService.saveSkuInfo(skuLsInfo);
    }

}
