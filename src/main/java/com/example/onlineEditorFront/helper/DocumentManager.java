/*
 *
 * (c) Copyright Ascensio System SIA 2019
 *
 * The MIT License (MIT)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.example.onlineEditorFront.helper;

import com.example.onlineEditorFront.config.OnlyOfficeConfig;
import com.example.onlineEditorFront.enums.FileTypeEnum;
import org.primeframework.jwt.Signer;
import org.primeframework.jwt.Verifier;
import org.primeframework.jwt.domain.JWT;
import org.primeframework.jwt.hmac.HMACSigner;
import org.primeframework.jwt.hmac.HMACVerifier;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DocumentManager {

    private static HttpServletRequest request;

    public static void init(HttpServletRequest req, HttpServletResponse resp) {
        request = req;
    }

    public static long getMaxFileSize() {
        return OnlyOfficeConfig.fileSizeMax > 0 ? OnlyOfficeConfig.fileSizeMax : 5 * 1024 * 1024;
    }

    public static List<String> GetFileExts() {
        List<String> res = new ArrayList<>();

        res.addAll(getViewedExts());
        res.addAll(getEditedExts());
        res.addAll(getConvertExts());

        return res;
    }

    public static List<String> getViewedExts() {
        return Arrays.asList(OnlyOfficeConfig.fileDocServiceViewDocs.split("\\|"));
    }

    public static List<String> getEditedExts() {
        return Arrays.asList(OnlyOfficeConfig.fileDocServiceEditDocs.split("\\|"));
    }

    public static List<String> getConvertExts() {
        return Arrays.asList(OnlyOfficeConfig.fileDocServiceConvertDocs.split("\\|"));
    }

    public static String CurUserHostAddress(String userAddress) {
        if (userAddress == null) {
            try {
                userAddress = InetAddress.getLocalHost().getHostAddress();
            } catch (Exception ex) {
                userAddress = "";
            }
        }

        return userAddress.replaceAll("[^0-9a-zA-Z.=]", "_");
    }

    public static String storagePath(String fileName) {
        fileName = fileName.split("/")[fileName.split("/").length-1];
        String serverPath = request.getSession().getServletContext().getRealPath("");
        String directory = serverPath + File.separator + OnlyOfficeConfig.storageFolder + File.separator;

        File file = new File(directory);

        if (!file.exists()) {
            file.mkdir();
        }

        //directory = directory + hostAddress + File.separator;
        directory = directory + File.separator;
        file = new File(directory);

        if (!file.exists()) {
            file.mkdir();
        }

        return directory + fileName;
    }

    public static String getCorrectName(String fileName) {
        String baseName = FileUtility.GetFileNameWithoutExtension(fileName);
        String ext = FileUtility.GetFileExtension(fileName);
        String name = baseName + ext;

        File file = new File(storagePath(name));

        for (int i = 1; file.exists(); i++) {
            name = baseName + " (" + i + ")" + ext;
            file = new File(storagePath(name));
        }

        return name;
    }

    public static String getFileUri(String fileName) {
        try {
            String serverPath = getServerUrl();
            // 返回文件在target下的地址，该地址可通过Uri直接访问
            return serverPath + "/" + OnlyOfficeConfig.storageFolder + "/" + URLEncoder.encode(fileName, java.nio.charset.StandardCharsets.UTF_8.toString()).replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    /**
     * 获取文档编辑服务域名
     * @return
     */
    public static String getServerUrl() {
        return OnlyOfficeConfig.onlineEditorDomainName + request.getContextPath();
    }

    public static String getCallback(String fileId, String accessToken) {
        String serverPath = getServerUrl();
        // 文件编辑的回调接口参数变更fileId的形式，对接文件服务器
        String query = "?type=track&fileId=" + fileId + "&access_token=" + accessToken;
        return serverPath + "/IndexServlet" + query;
    }

    public static String createToken(Map<String, Object> payloadClaims) {
        try {
            Signer signer = HMACSigner.newSHA256Signer(getTokenSecret());
            JWT jwt = new JWT();
            for (String key : payloadClaims.keySet()) {
                jwt.addClaim(key, payloadClaims.get(key));
            }
            return JWT.getEncoder().encode(jwt, signer);
        } catch (Exception e) {
            return "";
        }
    }

    public static Boolean tokenEnabled() {
        String secret = getTokenSecret();
        return secret != null && !secret.isEmpty();
    }

    private static String getTokenSecret() {
        return OnlyOfficeConfig.fileDocServiceSecret;
    }
}