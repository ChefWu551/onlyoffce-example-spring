package com.example.onlineEditorFront.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wuyuefeng
 * @brief 返回结果封装类
 * @email 565948592@qq.com
 * @date 2020-01-08
 */
@Data
public class CommonResult<T> implements Serializable {
    /**
     * 接口返回数据
     */
    private T data;

    /**
     * 接口调用状态
     */
    private Boolean success = true;

    /**
     * 错误信息
     */
    private String message;

    /**
     * 错误码
     */
    private String code;

    public Boolean isSuccess() {
        return this.success;
    }

    public void setErrorMsg(String errorMsg) {
        this.success = false;
        this.message = errorMsg;
    }
}
