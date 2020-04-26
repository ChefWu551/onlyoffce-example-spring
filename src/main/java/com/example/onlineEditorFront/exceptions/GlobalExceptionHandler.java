package com.example.onlineEditorFront.exceptions;


import com.example.onlineEditorFront.enums.ResponseEnum;
import com.example.onlineEditorFront.model.ResultBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理token认证
     *
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = NullPointerException.class)
    @ResponseBody
    public ResultBody nullPointExceptionHandler(HttpServletRequest req, Exception e) {
        ResponseEnum.BAD_REQUEST.setResultMsg(e.getMessage());
        return ResultBody.error(ResponseEnum.BAD_REQUEST);
    }

    /**
     * 处理token认证
     *
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = IllegalArgumentException.class)
    @ResponseBody
    public ResultBody illegalArgumentExceptionHandler(HttpServletRequest req, Exception e) {
        String message = e.getMessage();
        ResponseEnum.BAD_REQUEST.setResultMsg(message);
        return ResultBody.error(ResponseEnum.BAD_REQUEST);
    }


    /**
     * 处理其他异常
     *
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResultBody exceptionHandler(HttpServletRequest req, Exception e) {
        logger.error("未知异常！原因是:", e);
        return ResultBody.error(ResponseEnum.INTERNAL_SERVER_ERROR);
    }

}