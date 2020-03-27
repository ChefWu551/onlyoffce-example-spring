package com.example.onlineEditorFront.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.onlineEditorFront.model.Auth;
import com.example.onlineEditorFront.service.UserService;
import com.example.onlineEditorFront.utils.HttpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Value("${auth.url.register}")
    private String authUrlRegister;

    @Value("${auth.oauth.token}")
    private String authToken;

    @Value("${auth.rs.checkToken}")
    private String autoRsCheckToken;

    @Override
    public JSONObject register(String username, String password) {
        return getAuthResponseByUrl(authUrlRegister, username, password);
    }

    @Override
    public JSONObject getToken(String username, String password) {
        return getAuthResponseByUrl(authToken, username, password);
    }

    @Override
    public JSONObject getUserInfoByToken(String accessToken){
        Assert.hasText(accessToken, "accessToken不能为空");

        Auth auth = new Auth();
        Map<String, Object> parameterMap = auth.authzAccessTokenMap(accessToken);
        return JSONObject.parseObject(HttpUtil.sendGet(autoRsCheckToken, parameterMap));
    }

    /**
     * 通过请求业务url获取返回结果
     * @param url 请求的url
     * @param username 用户名
     * @param password 用户密码
     * @return
     */
    private JSONObject getAuthResponseByUrl(String url, String username, String password){
        Assert.hasText(url, "url不能为空");
        Assert.hasText(username, "用户名不能为空");
        Assert.hasText(password, "密码不能为空");

        Auth auth = new Auth();
        Map<String, Object> parameterMap = auth.authzMap(username, password);
        return JSONObject.parseObject(HttpUtil.sendPost(url, parameterMap));
    }


}
