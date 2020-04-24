package com.example.onlineEditorFront.service;

import com.alibaba.fastjson.JSONObject;
import com.example.onlineEditorFront.model.UserInfo;

public interface UserService {

    JSONObject register(String username, String password);

    JSONObject getToken(String username, String password);

    JSONObject getUserInfoByToken(String accessToken);

    UserInfo getUserInfo(String accessToken);
}
