package com.example.onlineEditorFront.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wuyuefeng
 * @brief 认证中心相关
 * @email 565948592@qq.com
 * @date 2020-01-08
 */
@Data
public class Auth {

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "固定参数，权限", notes = "固定值")
    private String scope = "read";

    @ApiModelProperty(value = "应用id", notes = "固定值")
    private String clientId = "online-editor-front";

    @ApiModelProperty(value = "秘钥", notes = "固定值")
    private String clientSecret = "online-editor-front";

    @ApiModelProperty(value = "注册类型", notes = "固定值")
    private String registerType = "password";

    @ApiModelProperty(value = "版本号", notes = "固定值")
    private String appVersion = "1.0.0";

    @ApiModelProperty(value = "token")
    private String accessToken;


    public Map<String,  Object> authzMap(String username, String password){
        Map<String,  Object> parameterMap = new HashMap<>();

        parameterMap.put("username", username);
        parameterMap.put("password", password);
        parameterMap.put("scope", getScope());
        parameterMap.put("client_id", getClientId());
        parameterMap.put("client_secret", getClientSecret());
        parameterMap.put("register_type", getRegisterType());
        parameterMap.put("grant_type", getRegisterType());
        parameterMap.put("appVersion", getAppVersion());

        return parameterMap;
    }

    public Map<String, Object> authzAccessTokenMap(String accessToken){
        Map<String,  Object> parameterMap = new HashMap<>();
        parameterMap.put("access_token", accessToken);
        parameterMap.put("appVersion", getAppVersion());
        return parameterMap;
    }
}
