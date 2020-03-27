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

/**
 * @program: OnlineEditors
 * @description:
 * @author: lvqiushi
 * @create: 2019-04-22 14:21
 **/
@Slf4j
public class UserInfoUtils {

    /**
     * OA获取用户信息地址
     */
    private final static String USER_QUERY_URL = "todo";

    /**
     * 测试环境认证中心地址
     */
    private final static String TOKEN_CHECK_URL = "http://api.test.todo.com/resource/rs/checkToken";

    /**
     * @param accessToken
     * @Description: 使用测试环境的token校验token
     * @return: model.CommonResult<model.UserInfo>
     * @Author: lvqiushi
     * @Date: 2019-05-13
     */
    public static CommonResult<UserInfo> getUserIdfoByToken(String accessToken) {
        CommonResult<UserInfo> result = new CommonResult<>();
        String url = TOKEN_CHECK_URL + "?access_token=" + accessToken;
        result = UserInfoUtils.sendJudgeToken(url);
        return result;
    }

    /**
     * @param url
     * @Description: 发送校验token请求
     * @return: model.CommonResult<model.UserInfo>
     * @Author: lvqiushi
     * @Date: 2019-05-13
     */
    public static CommonResult<UserInfo> sendJudgeToken(String url) {
        CommonResult<UserInfo> result = new CommonResult<>();
        UserInfo userInfo = new UserInfo();
        HttpGet get = new HttpGet(url);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpResponse checkToekn = httpClient.execute(get);

            StatusLine statusLine = checkToekn.getStatusLine();
            // 响应码
            int statusCode = statusLine.getStatusCode();

            // 请求成功
            if (statusCode == 200) {
                HttpEntity response = checkToekn.getEntity();
                JSONObject jsonObject = JSON.parseObject(EntityUtils.toString(response));

                if (jsonObject == null || !jsonObject.containsKey("success") || !jsonObject.getBoolean("success")) {
                    result.setErrorMsg("token错误");
                    return result;
                }
                userInfo.setUserId(jsonObject.getJSONObject("result").getInteger("userId"));
                userInfo.setUserName(jsonObject.getJSONObject("result").getString("username"));
                result.setData(userInfo);
            } else {
                result.setErrorMsg("token校验失败");
            }
        } catch (Exception ex) {
            log.error("", ex);
            result.setSuccess(false);
        } finally {
            if (get != null) {
                get.releaseConnection();
            }
        }
        return result;
    }

    /**
     * 获取用户信息
     * @param userId
     * @param accessToken
     * @return
     */
    // todo: 需要接入认证
    public static CommonResult<UserInfo> getUserInfo(Integer userId, String accessToken) {
        CommonResult<UserInfo> result = new CommonResult<>();
        // todo:需要接入认证
        return result;
    }

}
