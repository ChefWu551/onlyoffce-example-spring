package com.example.onlineEditorFront.controller;

import com.example.onlineEditorFront.service.EditorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
* @author wuyuefeng
* @brief 文件预览和编辑
* @email 565948592@qq.com
* @date 2020-01-08
*/
@Controller
public class EditorController {

    @Autowired
    EditorService editorService;

    @RequestMapping("EditorServlet")
    public String processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        return editorService.process(request, response);
    }

}
