package com.hbxy.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.hbxy.gmall.bean.*;
import com.hbxy.gmall.service.ManageService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class ManageController {

    @Reference
    private ManageService manageService;



    //http://localhost:8082/getCatalog1
    //返回所有一级分类名称
    @RequestMapping("getCatalog1")
    public List<BaseCatalog1> getCatalog1(){
        return manageService.getCatalog1();
    }

    //http://localhost:8082/getCatalog2?catalog1Id=2
    //@RequestParam = 获取参数
    //返回一级分类下的所有二级分类名称
    @RequestMapping("getCatalog2")
    public List<BaseCatalog2> getCatalog2(String catalog1Id,BaseCatalog2 baseCatalog2){
        //return manageService.getCatalog2(catalog1Id);
        return manageService.getCatalog2(baseCatalog2);
    }

    //http://localhost:8082/getCatalog3?catalog2Id=13
    //返回二级分类下的所有三级分类名称
    @RequestMapping("getCatalog3")
    public List<BaseCatalog3> getCatalog3(BaseCatalog3 baseCatalog3){
        return manageService.getCatalog3(baseCatalog3);
    }

    //http://localhost:8082/attrInfoList?catalog3Id=61
    //通过三级分类id返回商品
    @RequestMapping("attrInfoList")
    @ResponseBody
    public List<BaseAttrInfo> attrInfoList(String catalog3Id,BaseAttrInfo baseAttrInfo){
//        return manageService.getAttrInfoList(baseAttrInfo);
        return manageService.getAttrInfoList(catalog3Id);
    }

    //http://localhost:8082/saveAttrInfo
    //接受前台数据
    //页面传递数据是json，后端接收对象是java的Object  需要数据类型转换
    @RequestMapping("saveAttrInfo")
    public void savaAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        //调用服务层
        manageService.savaAttrInfo(baseAttrInfo);
    }

    //http://localhost:8082/getAttrValueList?attrId=100
    //修改--先回显数据  在修改  保存
    @RequestMapping("getAttrValueList")
    public List<BaseAttrValue> getAttrValueList(String attrId){
        //功能来讲
        //return manageService.getAttrValueList(attrId);
        //业务来讲:先查询baseAttrInfo
        BaseAttrInfo baseAttrInfo = manageService.getBaseAttrInfo(attrId);
        return baseAttrInfo.getAttrValueList();


    }




}
