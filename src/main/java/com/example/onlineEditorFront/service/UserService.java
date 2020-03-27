package com.example.onlineEditorFront.service;

import com.alibaba.fastjson.JSONObject;

public interface UserService {

    JSONObject register(String username, String password);

    JSONObject getToken(String username, String password);

    JSONObject getUserInfoByToken(String accessToken);
}
