package com.hbxy.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.hbxy.gmall.bean.BaseSaleAttr;
import com.hbxy.gmall.bean.SpuInfo;
import com.hbxy.gmall.service.ManageService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class SpuManageController {

    @Reference
    private ManageService manageService;

    //http://localhost:8082/spuList?catalog3Id=61
    @RequestMapping("spuList")
    public List<SpuInfo> getspuList(String catalog3Id){
        return manageService.getSpuInfoList(catalog3Id);
    }

    //http://localhost:8082/baseSaleAttrList  返回属性（加载销售属性列表）
    @RequestMapping("baseSaleAttrList")
    public List<BaseSaleAttr> getBaseSaleAttr(){
        return manageService.getBaseSaleAttrList();
    }

    //http://localhost:8082/saveSpuInfo  保存
    //spuinfo  spuimage  spuSaleAttr  spuSaleAttrValue
    @RequestMapping("saveSpuInfo")
    public void saveSpuInfo(@RequestBody SpuInfo spuInfo){
        manageService.saveSpuInfo(spuInfo);
    }

}
