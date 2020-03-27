package com.example.onlineEditorFront.service.impl;

import com.example.onlineEditorFront.config.OnlyOfficeConfig;
import com.example.onlineEditorFront.model.FileModel;
import com.example.onlineEditorFront.helper.DocumentManager;
import com.example.onlineEditorFront.model.CommonResult;
import com.example.onlineEditorFront.model.UserInfo;
import com.example.onlineEditorFront.service.EditorService;
import com.example.onlineEditorFront.utils.UserInfoUtils;
import com.example.onlineEditorFront.utils.UserUtil;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;

import static com.example.onlineEditorFront.utils.ErrorResult.commonResultReturn;

@Service
public class EditorServiceImpl extends HttpServlet implements EditorService {

    @Value("${file.service.download.url}")
    private String fileServiceDownloadUrl;

    private static final Logger LOGGER = LoggerFactory.getLogger(EditorServiceImpl.class);

    private static final Integer INVALID_USER_ID = -1;

    public String process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accessToken = "12973ad1b7a5d39f2b8809b22d29e39d";
        UserUtil userUtil = new UserUtil();

        // 获取token
//        String accessToken = userUtil.getAccessToken(request);
        if (accessToken == null || accessToken.length() == 0) {
            commonResultReturn(request, response, "缺少token");
            return "error";
        }

        // 根据token, 获取用户userId和名称
        Integer userId = getUserId(accessToken, request, response);
        UserInfo userInfo = getUserInfo(userId, accessToken);

        DocumentManager.init(request, response);

        String fileId = request.getParameter("fileId");
        if (fileId == null) {
            commonResultReturn(request, response, "缺少必要的参数：fieldId");
            return "error";
        }

        String uri = fileServiceDownloadUrl + fileId + "&access_token=" + accessToken;
        String fileRealName;
        String fileOriginName;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpResponse downLoadResponse = getHttpResponse(httpClient, uri);
            if (downLoadResponse == null) {
                commonResultReturn(request, response, "请求文件内容失败");
                return "error";
            }

            // 响应码
            int statusCode = downLoadResponse.getStatusLine().getStatusCode();
            LOGGER.info("下载文件 fileId = {}, access_token = {} ", fileId, accessToken);
            // 请求成功
            if (statusCode == 200) {
                // 获取文件上传时的文件名
                Header fileHeader = downLoadResponse.getFirstHeader("downloadFileName");
                // 获取文件实际存储的文件名，要进行uri转码
                Header fileRealNameHeader = downLoadResponse.getFirstHeader("downloadFileRealName");
                if (fileHeader == null || fileRealNameHeader == null) {
                    commonResultReturn(request, response, "调用文件服务，获取文件名称失败。");
                    return "error";
                }

                fileOriginName = URLDecoder.decode(fileHeader.getValue(), "UTF-8");
                fileRealName = fileRealNameHeader.getValue().split("\\\\")[1];
                LOGGER.info("fileId = {} 要下载文件名 = {}  实际存储名 = {} ", fileId, fileOriginName, fileRealName);

                // 如果要查看的文件在本地存在，则不需要再去文件服务器下载了
                try {
                    getLocalFile(fileRealName, downLoadResponse);
                } catch (Exception e) {
                    commonResultReturn(request, response, "加载本地文件时候出现错误。");
                    response.getWriter().write("Error: " + e.getMessage());
                }
            } else {
                commonResultReturn(request, response, "调用文件失败，请检查。");
                return "error";
            }
        } catch (Exception e) {
            LOGGER.error("接入文件服务失败", e);
            commonResultReturn(request, response, "获取要查看的文件失败");
            return "error";
        }

        FileModel fileModel = new FileModel(fileRealName, fileId, accessToken, fileOriginName, userId, userInfo);

        fileModel = modelSelect(fileModel, request);

        LOGGER.info("fileModel 构建结果为 = " + FileModel.Serialize(fileModel));

        request.setAttribute("file", fileModel);
        request.setAttribute("docserviceApiUrl", OnlyOfficeConfig.fileDocServiceUrlApi);
        return "editor";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Editor page";
    }

    // 根据token获取userId
    private Integer getUserId(String accessToken, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        CommonResult<UserInfo> userIdResult = UserInfoUtils.getUserIdfoByToken(accessToken);
        if (userIdResult.isSuccess() || userIdResult.getData() != null) {
            return userIdResult.getData().getUserId();
        } else {
            commonResultReturn(request, response, "token认证失败，解析错误。");
            return -1;
        }
    }

    // 根据userId和Token获取用户信息
    private UserInfo getUserInfo(Integer userId, String accessToken) {
        UserInfo userInfo = new UserInfo();
        userInfo.setName("testUser");
        return userInfo;
    }

    // 获取请求文件结果
    private HttpResponse getHttpResponse(CloseableHttpClient httpClient, String uri) {
        try {
            RequestConfig timeoutConfig = RequestConfig.custom()
                    .setConnectTimeout(5000).setConnectionRequestTimeout(1000)
                    .setSocketTimeout(5000).build();

            HttpGet httpGet = new HttpGet(uri);
            httpGet.setConfig(timeoutConfig);
            return httpClient.execute(httpGet);
        } catch (Exception e) {
            LOGGER.error("请求文件服务失败");
            e.printStackTrace();
        }

        return null;
    }

    // 读出文件
    private void getLocalFile(String fileRealName, HttpResponse downLoadResponse) throws IOException {
        File existFile = new File(DocumentManager.storagePath(fileRealName));
        if (!existFile.exists()) {
            HttpEntity entity = downLoadResponse.getEntity();
            File dest = new File(DocumentManager.storagePath(fileRealName));
            OutputStream output = new FileOutputStream(dest);
            int len = 0;
            byte[] ch = new byte[1024];
            try (InputStream input = entity.getContent()) {
                while ((len = input.read(ch)) != -1) {
                    output.write(ch, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 选择file打开模式
    private FileModel modelSelect(FileModel fileModel, HttpServletRequest request) {
        if ("embedded".equals(request.getParameter("mode"))) {
            fileModel.InitDesktop();
        }

        if ("view".equals(request.getParameter("mode"))) {
            fileModel.editorConfig.mode = "view";
            fileModel.document.permissions.edit = false;
            fileModel.document.permissions.review = false;
        }

        if (DocumentManager.tokenEnabled()) {
            fileModel.BuildToken();
        }

        return fileModel;
    }
}
