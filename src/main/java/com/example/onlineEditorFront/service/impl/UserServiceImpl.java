package com.example.onlineEditorFront.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.onlineEditorFront.model.UserInfo;
import com.example.onlineEditorFront.service.UserService;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {

    @Override
    public UserInfo getUserInfo(String accessToken) {

        UserInfo userInfo = new UserInfo();

        userInfo.setName("wuyuefeng");
        userInfo.setUserId(888);

        return userInfo;
    }

    @Override
    public JSONObject register(String username, String password) {
        return null;
    }

    @Override
    public JSONObject getToken(String username, String password) {
        return null;
    }

    @Override
    public JSONObject getUserInfoByToken(String accessToken) {
        return null;
    }


}
