package com.example.onlineEditorFront.enums;

import com.example.onlineEditorFront.service.BaseErrorInfoInterface;

/**
* @author wuyuefeng
* @brief 返回处理结果
* @email 565948592@qq.com
* @date 2020-04-26
*/
public enum ResponseEnum implements BaseErrorInfoInterface {

    // 数据操作错误定义
    SUCCESS("200", "成功!"),
    BAD_REQUEST("400", "客户端请求的语法错误"),
    UNAUTHORIZED("401", "需要token认证"),
    FORBIDDEN("403", "token认证失败"),
    NOT_FOUND("404", "未找到该资源!"),
    INTERNAL_SERVER_ERROR("500", "服务器内部错误"),
    SERVER_UNAVAILABLE("503", "服务器正忙，请稍后再试!");

    /**
     * 错误码
     */
    private String resultCode;
    /**
     * 错误描述
     */
    private String resultMsg;

    ResponseEnum(String resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    @Override
    public String getResultCode() {
        return resultCode;
    }

    @Override
    public String getResultMsg() {
        return resultMsg;
    }

    @Override
    public void setResultMsg(String msg) {
        this.resultMsg = msg;
    }

    @Override
    public String toString(){
        return "{" + this.resultCode + ", " + this.resultMsg + "}";
    }
}