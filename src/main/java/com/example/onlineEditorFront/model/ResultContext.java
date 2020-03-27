package com.example.onlineEditorFront.model;

import com.alibaba.fastjson.JSONObject;

/**
 * @Description:
 * @email 565948592@qq.com
 * @Date: Created on 2019/11/11
 */
public class ResultContext<T> {
    /**
     * 是否成功
     */
    private boolean success;
    /**
     * 接口返回的数据
     */
    private T data;
    /**
     * 接口返回的代码类型
     */
    private int code;
    /**
     * 接口返回的提示信息
     */
    private String message;

    /**
     * 提供默认构造
     */
    public ResultContext() {
        this.code = ResultCode.SUCCESS;
        this.success = true;
        this.message = "访问成功";
    }

    public ResultContext(String jsonObjectString) {
        JSONObject jsonObject = JSONObject.parseObject(jsonObjectString);
        this.code = (Integer) jsonObject.get("code");
        this.success = (Boolean) jsonObject.get("success");
        this.message = (String) jsonObject.get("msg");
        this.data = (T) jsonObject.get("result");
    }

    public static ResultContext returnFail() {
        ResultContext resultContext = new ResultContext();
        resultContext.setCode(ResultCode.FAIL);
        resultContext.setMessage("访问失败");
        resultContext.setSuccess(false);
        return resultContext;
    }

    public static ResultContext returnFail(int code, String message) {
        ResultContext resultContext = new ResultContext();
        resultContext.setCode(code);
        resultContext.setMessage(message);
        resultContext.setSuccess(false);
        return resultContext;
    }

    public static ResultContext returnSuccess() {
        return new ResultContext();
    }

    public static ResultContext<Object> returnSuccess(Object data) {
        ResultContext resultContext = new ResultContext();
        resultContext.setData(data);
        return resultContext;
    }

    public static ResultContext<Object> returnSuccess(Object data, String msg) {
        ResultContext resultContext = new ResultContext();
        resultContext.setData(data);
        resultContext.setMessage(msg);
        return resultContext;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
