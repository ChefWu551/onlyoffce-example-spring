package com.example.onlineEditorFront.service.impl;

import com.example.onlineEditorFront.helper.DocumentManager;
import com.example.onlineEditorFront.helper.FileUtility;
import com.example.onlineEditorFront.helper.ServiceConverter;
import com.example.onlineEditorFront.service.IndexService;
import com.example.onlineEditorFront.utils.UserUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Scanner;

import static com.example.onlineEditorFront.helper.FileUtility.getFileName;

@Service
public class IndexServiceImpl extends HttpServlet implements IndexService {

    @Value("${file.service.upload.url}")
    private String fileServiceUploadUrl;

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexServiceImpl.class);

    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("type");
        LOGGER.info("文档编辑收到请求  action=" + action);
        if (action == null) {
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }

        DocumentManager.init(request, response);
        PrintWriter writer = response.getWriter();

        switch (action.toLowerCase()) {
            case "upload":
                upload(request, response, writer);
                break;
            case "convert":
                convert(request, response, writer);
                break;
            case "track":
                track(request, writer);
                break;
        }
    }

    private static void upload(HttpServletRequest request, HttpServletResponse response, PrintWriter writer) {
        response.setContentType("text/plain");

        try {
            Part httpPostedFile = request.getPart("file");

            String fileName = "";
            for (String content : httpPostedFile.getHeader("content-disposition").split(";")) {
                if (content.trim().startsWith("filename")) {
                    fileName = content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
                }
            }

            long curSize = httpPostedFile.getSize();
            if (DocumentManager.getMaxFileSize() < curSize || curSize <= 0) {
                writer.write("{ \"error\": \"File size is incorrect\"}");
                return;
            }

            String curExt = FileUtility.GetFileExtension(fileName);
            if (!DocumentManager.GetFileExts().contains(curExt)) {
                writer.write("{ \"error\": \"File type is not supported\"}");
                return;
            }

            InputStream fileStream = httpPostedFile.getInputStream();

            fileName = DocumentManager.getCorrectName(fileName);
            String fileStoragePath = DocumentManager.storagePath(fileName);
            outPutStream(fileStream, fileStoragePath);

            writer.write("{ \"filename\": \"" + fileName + "\"}");

        } catch (IOException | ServletException e) {
            writer.write("{ \"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private static void convert(HttpServletRequest request, HttpServletResponse response, PrintWriter writer) {
        response.setContentType("text/plain");

        try {
            String fileUri = request.getParameter("fileUri");
            String fileName = getFileName(fileUri);
            String internalFileExt = request.getParameter("outputForm");
            String fileExt = FileUtility.GetFileExtension(fileName);

            if (DocumentManager.getConvertExts().contains(fileExt)) {
                String key = ServiceConverter.GenerateRevisionId(fileUri);

                String newFileUri = ServiceConverter.GetConvertedUri(fileUri, fileExt, internalFileExt, key, true);

                if (newFileUri.isEmpty()) {
                    writer.write("{ \"step\" : \"0\", \"filename\" : \"" + fileName + "\"}");
                    return;
                }

                String correctName = DocumentManager.getCorrectName(FileUtility.GetFileNameWithoutExtension(fileName) + internalFileExt);

                URL url = new URL(newFileUri);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                InputStream stream = connection.getInputStream();

                if (stream == null) {
                    throw new Exception("Stream is null");
                }

                outPutStream(stream, correctName);

                connection.disconnect();

                // todo: 同名文件要处理一下，删除旧的文件
                //remove source file ?
                File sourceFile = new File(DocumentManager.storagePath(fileName));
                sourceFile.delete();

                fileName = correctName;
            }
            // TODO: 输出是乱码要处理
            writer.write("{ \"filename\" : \"" + URLDecoder.decode(fileName, "GBK") + "\"}");

        } catch (Exception ex) {
            writer.write("{ \"error\": \"" + ex.getMessage() + "\"}");
        }
    }

    private void track(HttpServletRequest request, PrintWriter writer) {
        // 文件服务器上传文件地址
        String fileId = request.getParameter("fileId");
        String body;
        try {
            Scanner scanner = new Scanner(request.getInputStream());
            scanner.useDelimiter("\\A");
            body = scanner.hasNext() ? scanner.next() : "";
            scanner.close();
        } catch (Exception ex) {
            writer.write("get request.getInputStream error:" + ex.getMessage());
            return;
        }

        if (body.isEmpty()) {
            LOGGER.warn("empty request.getInputStream");
            writer.write("empty request.getInputStream");
            return;
        }

        JSONParser parser = new JSONParser();
        JSONObject jsonObj;

        try {
            Object obj = parser.parse(body);
            jsonObj = (JSONObject) obj;
        } catch (Exception ex) {
            writer.write("JSONParser.parse error:" + ex.getMessage());
            return;
        }

        // status 状态值含义
        // 0 - no document with the key identifier could be found,
        // 1 - document is being edited,
        // 2 - document is ready for saving,
        // 3 - document saving error has occurred,
        // 4 - document is closed with no changes,
        // 6 - document is being edited, but the current document state is saved,
        // 7 - error has occurred while force saving the document.
        int status = ((Long) jsonObj.get("status")).intValue();
        String downloadUri = (String) jsonObj.get("url");
        LOGGER.info("文档编辑 trace方法访问 status = {}  fileId = {}", status, fileId);

        int saved = 0;
        // MustSave, Corrupted
        if (status == 2 || status == 3) {
            UserUtil userUtil = new UserUtil();
            String accessToken = userUtil.getAccessToken(request);

            if (accessToken == null || accessToken.length() == 0) {
                writer.write("缺少token");
                return;
            }
            // 前端返回 文档编辑后的回写文件过来，这里需要调用文件服务器修改文件内容接口将新文件重新保存
            HttpPost httpPost = new HttpPost(fileServiceUploadUrl + "?access_token=" + accessToken);
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                URL url = new URL(downloadUri);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                InputStream stream = connection.getInputStream();

                if (stream == null) {
                    throw new Exception("Stream is null");
                }

                MultipartEntityBuilder fileBuilder = MultipartEntityBuilder.create();
                fileBuilder.setMode(HttpMultipartMode.RFC6532);
                fileBuilder.addBinaryBody("file", stream, ContentType.create("multipart/form-data"), getFileName(url.getPath()));
                fileBuilder.addTextBody("fileId", fileId);

                HttpEntity fileEntity = fileBuilder.build();
                httpPost.setEntity(fileEntity);

                HttpResponse uploadResponse = httpClient.execute(httpPost);

                StatusLine statusLine = uploadResponse.getStatusLine();
                // 响应码
                int statusCode = statusLine.getStatusCode();

                LOGGER.info("覆写文件请求返回相应 code=" + statusCode);
                // 请求成功
                if (statusCode == 200) {
                    HttpEntity entity = uploadResponse.getEntity();
                    LOGGER.info("调用文件服务override接口 返回结果为 " + EntityUtils.toString(entity));
                } else {
                    LOGGER.error("回写文件失败");
                }

                connection.disconnect();
            } catch (Exception ex) {
                LOGGER.error("回写文件失败", ex);
                saved = 1;
            } finally {
                httpPost.releaseConnection();
            }
        }

        writer.write("{\"error\":" + saved + "}");
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
        return "Handler";
    }

    // 输出文件
    private static void outPutStream(InputStream stream, String correctName){
        File convertedFile = new File(DocumentManager.storagePath(correctName));
        try (FileOutputStream out = new FileOutputStream(convertedFile)) {
            int read;
            final byte[] bytes = new byte[1024];
            while ((read = stream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }

            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
