package com.example.onlineEditorFront.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class UserUtil {

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
