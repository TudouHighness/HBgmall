package com.hbxy.gmall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;

import com.hbxy.gmall.bean.UserInfo;
import com.hbxy.gmall.config.LoginRequire;
import com.hbxy.gmall.passport.config.JwtUtil;
import com.hbxy.gmall.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PassportController {

    @Reference
    private UserService userService;

    @Value("${token.key}")
    private String key;

    // 获取用户点击的url 后面的参数
    // https://passport.jd.com/new/login.aspx?ReturnUrl=https%3A%2F%2Fwww.jd.com%2F
    // http://localhost:8087/index?originUrl=https%3A%2F%2Fwww.jd.com%2F
    @RequestMapping("index")
    @LoginRequire(loginMySelf = true)
    public String index(HttpServletRequest request) {
        String originUrl = request.getParameter("originUrl");
        System.out.println(originUrl);
        // 保存
        request.setAttribute("originUrl", originUrl);
        return "index";
    }

    @RequestMapping("indexMySelf")
    public String indexMySelf(HttpServletRequest request) {
        String originUrl = request.getParameter("originUrl");
        System.out.println(originUrl);
        // 保存
        request.setAttribute("originUrl", originUrl);
        return "index";
    }


    // 如何得到表单提交过来的数据
    @RequestMapping("login")
    @ResponseBody
    public String login(UserInfo userInfo, HttpServletRequest request) {
        // 调用服务层
        UserInfo info = userService.login(userInfo);
        if (info != null) {
            // data --- token
            // 参数
            HashMap<String, Object> map = new HashMap<>();
            map.put("userId",info.getId());
            map.put("nickName",info.getNickName());
            // 服务的Ip 地址 配置nginx 服务器代理
            String salt = request.getHeader("X-forwarded-for");
            String token = JwtUtil.encode(key, map, salt);
            System.out.println(token);
            return token;
        }
        return "fail";

    }
    // 直接将token ，salt 以参数的形式传入到控制器
    // http://passport.hbxy.com/verify?token=xxxx&salt=xxx
    @RequestMapping("verify")
    @ResponseBody
    public String verify(HttpServletRequest request){
        // 从token 中获取userId  --- {解密token}  Map<String, Object> map1 = JwtUtil.decode(token, key, salt);
        String token = request.getParameter("token");
        String salt = request.getParameter("salt");

        Map<String, Object> map = JwtUtil.decode(token, key, salt);
        // 判断map
        if(map!=null && map.size()>0){
            // 从token 中解密 出来的userId
            String userId = (String) map.get("userId");
            // 调用服务层
            UserInfo userInfo = userService.verfiy(userId);

            if (userInfo!=null){
                return "success";
            }
        }
        return "fail";
    }


    @RequestMapping("signOut")
    public String signOut(String key){
        System.out.println(key);
        userService.signOut(key);
        return "index";
    }



}
