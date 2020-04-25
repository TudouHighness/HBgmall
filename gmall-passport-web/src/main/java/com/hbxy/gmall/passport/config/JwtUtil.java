package com.hbxy.gmall.passport.config;


import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.Base64UrlCodec;

import java.util.Map;

public class JwtUtil {
    /**
     * 生成token
     * @param key 公共部分
     * @param param 私有部分
     * @param salt 服务Ip
     * @return
     */
    public static String encode(String key, Map<String,Object> param, String salt){
        if(salt!=null){
            key+=salt;
        }
        // 将最新的key 进行base64UrlCodec
        Base64UrlCodec base64UrlCodec = new Base64UrlCodec();
        key = base64UrlCodec.encode(key);

        JwtBuilder jwtBuilder = Jwts.builder().signWith(SignatureAlgorithm.HS256,key);

        jwtBuilder = jwtBuilder.setClaims(param);
        // 由三部分组成
        String token = jwtBuilder.compact();
        return token;

    }


    /**
     * 解密token
     * @param token
     * @param key
     * @param salt
     * @return
     */
    public  static Map<String,Object> decode(String token , String key, String salt){
        Claims claims=null;
        if (salt!=null){
            key+=salt;
        }
        // 将最新的key 进行base64UrlCodec
        Base64UrlCodec base64UrlCodec = new Base64UrlCodec();
        key = base64UrlCodec.encode(key);
        try {
            claims= Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        } catch ( JwtException e) {
            return null;
        }
        return  claims;
    }

}
