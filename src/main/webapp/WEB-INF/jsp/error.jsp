<%@page import="com.example.onlineEditorFront.model.CommonResult"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>异常页面</title>
        <% CommonResult result = (CommonResult) request.getAttribute("result"); %>
    </head>
    <body>
            error Message:<H3><%= result.getMessage() %></H3>
    </body>
</html>
