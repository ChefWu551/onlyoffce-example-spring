package com.example.onlineEditorFront.service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface IndexService {
    void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
