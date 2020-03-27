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


package com.example.onlineEditorFront.model;

import com.alibaba.fastjson.JSONObject;
import com.example.onlineEditorFront.helper.DocumentManager;
import com.example.onlineEditorFront.helper.FileUtility;
import com.example.onlineEditorFront.helper.ServiceConverter;

import java.util.HashMap;
import java.util.Map;

public class FileModel {
    public String type = "desktop";
    public String documentType;
    public Document document;
    public EditorConfig editorConfig;
    public String token;

    public FileModel(String fileName, String fileId, String accessToken, String fileOriginName, Integer userId, UserInfo userInfo) {
        if (fileName == null) fileName = "";
        fileName = fileName.trim();
        // 设置当前用户的信息
        documentType = FileUtility.GetFileType(fileName).toString().toLowerCase();

        document = new Document();
        document.title = fileOriginName;
        document.url = DocumentManager.getFileUri(fileName);
        document.fileType = FileUtility.GetFileExtension(fileName).replace(".", "");
        document.key = ServiceConverter.GenerateRevisionId(userId + "/" + fileName);

        editorConfig = new EditorConfig();
        if (!DocumentManager.getEditedExts().contains(FileUtility.GetFileExtension(fileName))) {
            editorConfig.mode = "view";
        }

        editorConfig.callbackUrl = DocumentManager.getCallback(fileId, accessToken);
        editorConfig.user.id = String.valueOf(userId);
        editorConfig.user.name = userInfo.getName();
        editorConfig.customization.customer.logo = DocumentManager.getServerUrl() + "/css/img/logo.jpg";
        editorConfig.customization.logo.image = DocumentManager.getServerUrl() + "/css/img/logo.jpg";
    }

    public void InitDesktop() {
        type = "embedded";
        editorConfig.InitDesktop(document.url);
    }

    public void BuildToken() {
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("documentType", documentType);
        map.put("document", document);
        map.put("editorConfig", editorConfig);

        token = DocumentManager.createToken(map);
    }

    public class Document {
        public String title;
        public String url;
        public String fileType;
        public String key;
        public permissions permissions;

        public Document() {
            permissions = new permissions();
        }

        public class permissions {
            public Boolean edit = true;
            public Boolean comment = true;
            public Boolean download = true;
            public Boolean fillForms = true;
            public Boolean print = true;
            public Boolean review = true;
        }
    }

    public class EditorConfig {
        public String mode = "edit";
        public String callbackUrl;
        public User user;
        public Customization customization;
        public Embedded embedded;
        /**
         * 语言选项
         */
        public String lang = "zh-CN";

        public EditorConfig() {
            user = new User();
            customization = new Customization();
        }

        public void InitDesktop(String url) {
            embedded = new Embedded();
            embedded.saveUrl = url;
            embedded.embedUrl = url;
            embedded.shareUrl = url;
            embedded.toolbarDocked = "top";
        }

        public class User {
            public String id;
            public String name = "no_user";
        }

        // 个性化配置，高版本的Onlyoffice不支持这种配置的
        public class Customization {
            public Boolean goback = false;

            public Customer customer;

            public Boolean help = false;

            public String loaderLogo = DocumentManager.getServerUrl() + "/css/img/logo.jpg";

            public Logo logo;

            public Customization() {
                customer = new Customer();
                logo = new Logo();
                customer.address = "公司地址";
                customer.mail = "565948592@qq.com";
                customer.www = "https://testUrl.com";
                customer.name = "测试名称";
                customer.info = "onlyoffice前置服务信息编辑";

                logo.imageEmbedded = "css/img/shandong.png";
                logo.url = "https://your-offical-website";
            }

            public class Customer {
                public String address;

                public String info;

                public String logo;

                public String mail;

                public String name;

                public String www;
            }

            public class Logo {
                public String image;

                public String imageEmbedded;

                public String url;
            }
        }

        public class Embedded {
            public String saveUrl;
            public String embedUrl;
            public String shareUrl;
            public String toolbarDocked;
        }
    }


    public static String Serialize(FileModel model) {
        return JSONObject.toJSONString(model);
    }
}
