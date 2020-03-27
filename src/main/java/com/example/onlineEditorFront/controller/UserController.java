package com.example.onlineEditorFront.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.onlineEditorFront.model.ResultContext;
import com.example.onlineEditorFront.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
* @author wuyuefeng
* @brief 用户认证和权限控制
* @email 565948592@qq.com
* @date 2020-01-08
*/
@Api(tags = "用户管理")
@Controller
@RequestMapping("user")
@ResponseBody
public class UserController {

    @Autowired
    UserService userService;

    @ApiOperation("注册用户")
    @PostMapping("register")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "username", value = "用户名称", required = true),
            @ApiImplicitParam(paramType = "query", name = "password", value = "用户密码", required = true),
    })
    public ResultContext register(String username, String password){
        JSONObject jsonObject = userService.register(username, password);
        return new ResultContext(jsonObject.toString());
    }

    @ApiOperation("判断用户是否注册")
    @PostMapping("existInfo")
    public ResultContext existInfo(){
        return new ResultContext();
    }

    @ApiOperation("账号密码登录")
    @PostMapping("getToken")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "username", value = "用户名称", required = true),
            @ApiImplicitParam(paramType = "query", name = "password", value = "用户密码", required = true),
    })
    public ResultContext getToken(String username, String password){
        JSONObject result = userService.getToken(username, password);
        return new ResultContext(result.toString());
    }

    @ApiOperation("根据token获取user信息")
    @PostMapping("getUserInfoByToken")
    public ResultContext getUserInfoByToken(String accessToken){
        JSONObject result = userService.getUserInfoByToken(accessToken);
        return new ResultContext(result.toString());
    }


    @ApiOperation("删除用户")
    @PostMapping("delete")
    public ResultContext delete(){
        return new ResultContext();
    }

    @ApiOperation("修改密码")
    @PostMapping("updatePassword")
    public ResultContext updatePassword(){
        return new ResultContext();
    }

}
