package com.example.onlineEditorFront.service;

/**
 * @Author：JCccc
 * @Description：此接口用于返回码枚举使用
 * @Date： created in 15:11 2019/5/3
 */

public interface BaseErrorInfoInterface {
    /** 错误码*/
    String getResultCode();

    /** 错误描述*/
    String getResultMsg();
}