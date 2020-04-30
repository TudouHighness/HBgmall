package com.hbxy.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.hbxy.gmall.bean.CartInfo;
import com.hbxy.gmall.bean.SkuInfo;
import com.hbxy.gmall.config.CookieUtil;
import com.hbxy.gmall.config.LoginRequire;
import com.hbxy.gmall.service.CartService;
import com.hbxy.gmall.service.ManageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
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

    @RequestMapping("cartList")
    @LoginRequire(autoRedirect = false)
    public String cartList(HttpServletRequest request){
        List<CartInfo> cartInfoList = new ArrayList<>();
        //获取用户id
        String userId = (String) request.getAttribute("userId");

        if (userId!=null){
            //登录  获取购物车
            //先获取未登录购物车是否有数据
            String userTempId = CookieUtil.getCookieValue(request, "user-key", false);
            //定义未登录
            List<CartInfo> cartInfoNoLoginList = new ArrayList<>();
            if (!StringUtils.isEmpty(userTempId)){
                //获取未登录购物车
                cartInfoNoLoginList = cartService.getCartList(userTempId);
                //判断集合中是否有数据
                if (cartInfoNoLoginList!=null&&cartInfoNoLoginList.size()>0){
                    //购物车中有数据  合并
                    cartInfoList=cartService.mergeToCartList(cartInfoNoLoginList,userId);
                    //删除未登录购物车数据
                    cartService.deleteCartList(userTempId);
                }
            }
            if (userTempId==null || (cartInfoNoLoginList==null || cartInfoNoLoginList.size()==0) ){
                // 说明未登录没有数据， 直接获取数据库！
                cartInfoList = cartService.getCartList(userId);
            }
        }else{
            //未登录  cookie中
            String userTempId = CookieUtil.getCookieValue(request, "user-key", false);
            if (!StringUtils.isEmpty(userTempId)){
                //未登录  获取购物车
                cartInfoList = cartService.getCartList(userTempId);
            }

        }

        request.setAttribute("cartInfoList",cartInfoList);
        return "cartList";
    }
}
