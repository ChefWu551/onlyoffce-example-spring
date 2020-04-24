package com.example.onlineEditorFront.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.onlineEditorFront.model.CommonResult;
import com.example.onlineEditorFront.model.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
* @author wuyuefeng
* @brief 用户处理
* @email wuyuefeng@thundersdata.com
* @date 2020-04-24
*/
@Slf4j
@Component
public class UserUtils {

    /**
     * 获取request中的accessToken
     *
     * @param request
     * @return
     */
    public String getAccessToken(HttpServletRequest request) {
        String accessToken = request.getParameter("access_token");

        if (accessToken == null || accessToken.length() < 1) {
            if (request.getCookies() != null && request.getCookies().length > 0) {
                Cookie[] cookies = request.getCookies();
                for (Cookie cookie : cookies) {
                    // cookie的key现在是写死的
                    if (cookie.getName().toLowerCase().startsWith("__token_assist-poverty")) {
                        accessToken = cookie.getValue();
                        break;
                    }
                }
            }
        }

        return accessToken;
    }
}
