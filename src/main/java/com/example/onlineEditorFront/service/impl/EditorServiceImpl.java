package com.example.onlineEditorFront.service.impl;

import com.example.onlineEditorFront.config.OnlyOfficeConfig;
import com.example.onlineEditorFront.model.*;
import com.example.onlineEditorFront.helper.DocumentManager;
import com.example.onlineEditorFront.service.EditorService;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.*;
import java.net.URLDecoder;
import java.util.regex.Matcher;

@Service
public class EditorServiceImpl extends HttpServlet implements EditorService {

    @Value("${file.service.download.url}")
    private String fileServiceDownloadUrl;

    private static final Logger LOGGER = LoggerFactory.getLogger(EditorServiceImpl.class);

    public String process(HttpServletRequest request, HttpServletResponse response) throws IOException {

        DocumentManager.init(request, response);

        String fileId = request.getParameter("fileId");
        String model = request.getParameter("mode");
        Assert.isTrue(!(ObjectUtils.isEmpty(fileId) || ObjectUtils.isEmpty(model)), "缺少必要的参数: fileId 或 model");

        String accessToken = request.getParameter("accessToken");
        Integer userId = Integer.valueOf(request.getParameter("userId"));
        String userName = request.getParameter("userName");

        String uri = fileServiceDownloadUrl + fileId + "&access_token=" + accessToken;

        HttpResponse downLoadResponse = getHttpResponse(uri);
        Assert.notNull(downLoadResponse, "请求文件服务失败");

        // 响应码
        Assert.isTrue(downLoadResponse.getStatusLine().getStatusCode() == 200, "请求文件服务失败");
        LOGGER.info("下载文件 fileId = {}, access_token = {} ", fileId, accessToken);

        // 获取文件实际名称和上传时的文件名
        Header fileHeader = downLoadResponse.getFirstHeader("downloadFileName");
        Header fileRealNameHeader = downLoadResponse.getFirstHeader("downloadFileRealName");

        String fileOriginName = URLDecoder.decode(fileHeader.getValue(), "UTF-8");
        String[] fileRealNameDir = fileRealNameHeader.getValue().split(Matcher.quoteReplacement(File.separator));
        String fileRealName = fileRealNameDir[fileRealNameDir.length - 1];
        LOGGER.info("fileId = {} 要下载文件名 = {}  实际存储名 = {} ", fileId, fileOriginName, fileRealName);

        // 如果要查看的文件在本地存在，则不需要再去文件服务器下载了
        getLocalFile(fileRealName, downLoadResponse);

        FileModel fileModel = new FileModel(fileRealName, fileId, accessToken, fileOriginName, userId, userName, model);
        LOGGER.info("fileModel 构建结果为 = " + FileModel.Serialize(fileModel));

        request.setAttribute("file", fileModel);
        request.setAttribute("docserviceApiUrl", OnlyOfficeConfig.fileDocServiceUrlApi);

        return "editor";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        process(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        process(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Editor page";
    }

    // 获取请求文件结果
    private HttpResponse getHttpResponse(String uri) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
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

            int len;
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

}
