package com.example.onlineEditorFront.utils;

import com.example.onlineEditorFront.model.CommonResult;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 错误类型返回
 */
public class ErrorResult {

    private static CommonResult commonResult = new CommonResult();

    public static void commonResultReturn(HttpServletRequest request, HttpServletResponse response, String errorMes) throws ServletException, IOException{
        commonResult.setErrorMsg(errorMes);
        request.setAttribute("result", commonResult);
        request.getRequestDispatcher("error").forward(request, response);
    }

}
