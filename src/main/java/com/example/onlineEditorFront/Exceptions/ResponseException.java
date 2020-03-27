package com.example.onlineEditorFront.Exceptions;

import com.example.onlineEditorFront.model.ResultCode;
import com.example.onlineEditorFront.model.ResultContext;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
* @author wuyuefeng
* @brief 自定义返回异常
* @email 565948592@qq.com
* @date 2020-01-10
*/

@ControllerAdvice
public class ResponseException {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResultContext handle(Exception e) {
        e.printStackTrace();
        return ResultContext.returnFail(ResultCode.FAIL, e.getMessage());
    }
}
