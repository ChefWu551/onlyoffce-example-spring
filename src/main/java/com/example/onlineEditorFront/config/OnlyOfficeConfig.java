package com.example.onlineEditorFront.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
* @author wuyuefeng
* @brief onlyoffice document server 配置信息
* @email 565948592@qq.com
* @date 2020-01-09
*/
@Configuration
@Data
public class OnlyOfficeConfig {

    // 文件最大容量允许
    public static Integer fileSizeMax;

    @Value("${file.size.max}")
    public void setFileSizeMax(Integer value){
        fileSizeMax = value;
    }

    // 当前服务存储临时文档的目录地址
    public static String storageFolder;

    @Value("${storage.folder}")
    public void setStorageFolder(String value){
        storageFolder = value;
    }


    public static String fileDocServiceViewDocs;

    @Value("${file.doc.service.view.docs}")
    public void setFileDocServiceViewDocs(String value){
        fileDocServiceViewDocs = value;
    }

    public static String fileDocServiceEditDocs;

    @Value("${file.doc.service.edit.docs}")
    public void setFileDocServiceEditDocs(String value){
        fileDocServiceEditDocs = value;
    }

    public static String fileDocServiceConvertDocs;

    @Value("${file.doc.service.convert.docs}")
    public void setFileDocServiceConvertDocs(String value){
        fileDocServiceConvertDocs = value;
    }

    public static Integer fileDocServiceTimeout;

    @Value("${file.doc.service.timeout}")
    public void setFileDocServiceTimeout(Integer value){
        fileDocServiceTimeout = value;
    }

    // 文件类型转换地址
    public static String fileDocServiceUrlConvert;

    @Value("${file.doc.service.url.convert}")
    public void setFileDocServiceUrlConvert(String value){
        fileDocServiceUrlConvert = value;
    }

    // onlyoffice document server 缓存文件路径
    public static String fileDocServiceUrlTempStorage;

    @Value("${file.doc.service.url.temp.storage}")
    public void setFileDocServiceUrlTempStorage(String value){
        fileDocServiceUrlTempStorage = value;
    }

    // onlyoffice document server api
    public static String fileDocServiceUrlApi;

    @Value("${file.doc.service.url.api}")
    public void setFileDocServiceUrlApi(String value){
        fileDocServiceUrlApi = value;
    }

    public static String fileDocServiceUrlPreload;

    @Value("${file.doc.service.url.preload}")
    public void setFileDocServiceUrlPreload(String value){
        fileDocServiceUrlPreload = value;
    }

    public static String onlineEditorDomainName;

    @Value("${online.editor.domain.name}")
    public void setOnlineEditorDomainName(String value){
        onlineEditorDomainName = value;
    }

    public static String fileDocServiceSecret;

    @Value("${file.doc.service.secret}")
    public void setFileDocServiceSecret(String value){
        fileDocServiceSecret = value;
    }

}
