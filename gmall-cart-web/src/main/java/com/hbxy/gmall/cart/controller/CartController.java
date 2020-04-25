package com.hbxy.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.hbxy.gmall.bean.SkuInfo;
import com.hbxy.gmall.config.CookieUtil;
import com.hbxy.gmall.config.LoginRequire;
import com.hbxy.gmall.service.CartService;
import com.hbxy.gmall.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class CartController {

    @Reference
    private CartService cartService;

    @Reference
    private ManageService manageService;

    @RequestMapping("addToCart")
    @LoginRequire(autoRedirect = false)
    public String addToCart(HttpServletRequest request, HttpServletResponse response){
        //得到前台数据
        String skuNum = request.getParameter("skuNum");
        String skuId = request.getParameter("skuId");

        //获取用户id
        String userId = (String) request.getAttribute("userId");
        //判断用户是否登录
        if (userId==null){
            //用户未登录，存储一个临时的用户id，存储在cookie中
            userId = CookieUtil.getCookieValue(request, "user-key", false);

            //未登录情况下，没添加商品
            if(userId==null){
                //存储一个临时的用户id，存储在cookie中
                String tempUserId = UUID.randomUUID().toString().replace("-","");
                CookieUtil.setCookie(request,response,"user-key",tempUserId,7*24*3600,false);

            }

        }
        cartService.addToCart(skuId,userId,Integer.parseInt(skuNum));

        SkuInfo skuInfo = manageService.getSkuInfo(skuId);
        request.setAttribute("skuInfo",skuInfo);
        request.setAttribute("skuNum",skuNum);
        return "success";
    }
}
