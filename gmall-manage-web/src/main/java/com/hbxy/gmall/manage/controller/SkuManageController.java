package com.hbxy.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.hbxy.gmall.bean.SkuInfo;
import com.hbxy.gmall.bean.SpuImage;
import com.hbxy.gmall.bean.SpuSaleAttr;
import com.hbxy.gmall.service.ManageService;
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
    }

}
