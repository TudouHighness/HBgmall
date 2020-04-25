package com.hbxy.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.hbxy.gmall.bean.SkuInfo;
import com.hbxy.gmall.bean.SkuSaleAttrValue;
import com.hbxy.gmall.bean.SpuSaleAttr;
import com.hbxy.gmall.config.LoginRequire;
import com.hbxy.gmall.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@Controller
public class itemController {

    @Reference
    private ManageService manageService;

    @RequestMapping("{skuId}.html")
    @LoginRequire
    public String item(@PathVariable String skuId, HttpServletRequest request){
        //获取skuId
        System.out.println(skuId);

        //将商品的图片列表封装到skuInfo的skuImageList
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);

        //查询销售属性销售属性值 并锁定
        List<SpuSaleAttr> spuSaleAttrList = manageService.getSpuSaleAttrListCheckBySku(skuInfo);

        //查询销售属性值与skuId组合的数据集合
        List<SkuSaleAttrValue> skuSaleAttrValueListBySpu=manageService.getSkuSaleAttrValueListBySpu(skuInfo.getSpuId());

        //拼接字符串
        String key="";
        HashMap<String, String> map = new HashMap<>();
        if (skuSaleAttrValueListBySpu!=null && skuSaleAttrValueListBySpu.size()>0){
            //{"xxx|xxx":"xx","xxx|xxx":"xx"}
            //key = 123|123  value=1
            //map.put(key,value);  map --> josn
            //对应的拼接规则，1.如果skuId与下一个skuId不相同时停止  2.当循环到结合末尾停止拼接
            //第一次循环 key= 123
            //第二次   key=123|123
            //第三次  map.put... 清空key
            for (int i = 0;i < skuSaleAttrValueListBySpu.size();i++){
                SkuSaleAttrValue skuSaleAttrValue = skuSaleAttrValueListBySpu.get(i);
                //什么时候拼接
                if (key.length()!=0){
                    key+="|";
                }
                //拼接key
                key+=skuSaleAttrValue.getSaleAttrValueId();

                //什么时候停止拼接
                if ((i+1)== skuSaleAttrValueListBySpu.size()||!skuSaleAttrValue.getSkuId().equals(skuSaleAttrValueListBySpu.get(i+1).getSkuId())){
                    map.put(key,skuSaleAttrValue.getSkuId());
                    key="";

                }

            }
        }
        //转换为josn
        String valuesSkuJson  = JSON.toJSONString(map);
        System.out.println(valuesSkuJson);
        request.setAttribute("valuesSkuJson",valuesSkuJson);
        //保存到作用域
        request.setAttribute("spuSaleAttrList",spuSaleAttrList);
        //保存skuInfo
        request.setAttribute("skuInfo",skuInfo);
        //返回商品详情页面
        return "item";
    }

}
