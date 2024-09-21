package com.meetu.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JsonWebTokenInterceptor implements HandlerInterceptor {

    @Autowired
    private JsonWebTokenUtility jsonWebTokenUtility;
    
    private JSONObject processAuthorizationHeader(String auth) throws JSONException {
        if(auth != null && auth.length() != 0) {
            String token = auth.substring(7);
            String data = jsonWebTokenUtility.validateToken(token);
            if(data != null && data.length() != 0) {
                return new JSONObject(data);
            }
        }
        return null;
    }
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws JSONException {
        String method = request.getMethod();
        if (!"OPTIONS".equals(method)) { // 忽略 OPTIONS 請求
            String auth = request.getHeader("Authorization");
            
            JSONObject userJson = processAuthorizationHeader(auth); // 驗證並獲取用戶信息
            if (userJson == null || userJson.length() == 0) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 禁止訪問
                response.setHeader("Access-Control-Allow-Credentials", "true");
                response.setHeader("Access-Control-Allow-Origin", "*");
                response.setHeader("Access-Control-Allow-Headers", "*");
                return false;
            }
        }
        return true;
    }

}
