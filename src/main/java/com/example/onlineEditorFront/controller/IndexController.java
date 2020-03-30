package com.example.onlineEditorFront.controller;

import com.example.onlineEditorFront.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
* @author wuyuefeng
* @brief 文件转换、下载、编辑跟踪
* @email 565948592@qq.com
* @date 2020-01-08
*/
@Controller
public class IndexController extends HttpServlet {

    @Autowired
    IndexService indexService;

    @RequestMapping("IndexServlet")
    public String processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        return indexService.process(request, response);
    }
}
