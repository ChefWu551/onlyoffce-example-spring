package com.example.onlineEditorFront.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author：wuyuefeng
 * @date ：Created in 2018-10-26
 */
@Slf4j
public class HttpUtil {
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36";
    public static final String UTF8 = "utf-8";

    /**
     * 超时设置
     */
    private static final RequestConfig REQUEST_CONFIG = RequestConfig.custom()
            .setConnectTimeout(10000)
            .setConnectionRequestTimeout(10000)
            .setSocketTimeout(20000)
            .build();

    /**
     * 编码设置
     */
    private static final ConnectionConfig CONNECTION_CONFIG = ConnectionConfig.custom()
            .setMalformedInputAction(CodingErrorAction.IGNORE)
            .setUnmappableInputAction(CodingErrorAction.IGNORE)
            .setCharset(StandardCharsets.UTF_8)
            .build();

    private static HttpClientBuilder getBuilder(List<Header> headerList) {
        return HttpClients.custom()
                .setDefaultConnectionConfig(CONNECTION_CONFIG)
                .setDefaultHeaders(headerList)
                .setDefaultRequestConfig(REQUEST_CONFIG);
    }

    /**
     * 请求头设置
     */
    private static List<Header> getHeaderList() {
        List<Header> headers = new ArrayList<>();
        Header header = new BasicHeader("User-Agent", USER_AGENT);
        Header contentType = new BasicHeader("Content-Type", "application/x-www-form-urlencoded");
        headers.add(header);
        headers.add(contentType);

        return headers;
    }

    /**
     * 将url数据携带格式拼接成字符串
     *
     * @param paramMap map （请求名称：请求内容）
     * @return 请求格式字符串
     */
    private static String formatUrlParam(Map<String, Object> paramMap) {
        StringBuilder stringBuilder = new StringBuilder();
        paramMap.forEach((k, v) -> {
            try {
                stringBuilder.append(k).append("=").append(URLEncoder.encode(v.toString(), "utf8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });

        String param = stringBuilder.toString();
        return param.substring(0, param.length() - 1);
    }

    private static String getRequestResult(HttpUriRequest httpUriRequest) {
        String result = null;
        List<Header> headerList = Arrays.asList(httpUriRequest.getAllHeaders());
        if (CollectionUtils.isEmpty(headerList)) {
            headerList = getHeaderList();
        }

        try (CloseableHttpClient httpClient = getBuilder(headerList).build();
             CloseableHttpResponse response = httpClient.execute(httpUriRequest)) {
            HttpEntity httpEntity = response.getEntity();
            result = EntityUtils.toString(httpEntity);
        } catch (IOException e) {
            log.error("", e);
        }

        return result;
    }

    /**
     * 发送HttpPost请求，参数为json字符串
     *
     * @param url     请求地址
     * @param jsonStr json字符串
     * @return
     */
    public static String sendPost(String url, String jsonStr) {
        return sendPost(url, jsonStr, new HashMap<>());
    }

    /**
     * 发送HttpPost请求
     *
     * @param url    请求地址
     * @param params 表单参数
     * @return
     */
    public static String sendPost(String url, Map<String, Object> params) {
        return sendPost(url, params, new HashMap<>());
    }

    /**
     * 发送HttpGet请求
     *
     * @param url      请求地址
     * @param paramMap 表单参数
     * @return
     */
    public static String sendGet(String url, Map<String, Object> paramMap) {
        return sendGet(url, paramMap, new HashMap<>());
    }

    /**
     * 发送HttpGet请求
     *
     * @param url      请求地址
     * @param paramMap 表单参数
     * @param headers  请求头
     * @return
     */
    public static String sendGet(String url, Map<String, Object> paramMap, Map<String, String> headers) {
        url = getGetUrl(url, paramMap);

        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeaders(convertMap2Headers(headers));

        return getRequestResult(httpGet);
    }

    /**
     * 发送HttpPost请求，参数为json字符串(自定义请求头)
     *
     * @param url     请求地址
     * @param jsonStr json字符串
     * @param headers 请求头
     * @return
     */
    public static String sendPost(String url, String jsonStr, Map<String, String> headers) {
        // 设置entity
        StringEntity stringEntity = new StringEntity(jsonStr, Consts.UTF_8);
        stringEntity.setContentType("application/json");

        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(stringEntity);

        httpPost.setHeaders(convertMap2Headers(headers));
        httpPost.setHeader("Content-Type", "application/json");

        return getRequestResult(httpPost);
    }

    /**
     * 发送HttpPost请求(自定义请求头)
     *
     * @param url      请求地址
     * @param paramMap 表单参数
     * @param headers  请求头
     * @return
     */
    public static String sendPost(String url, Map<String, Object> paramMap, Map<String, String> headers) {
        // 设置entity
        List<NameValuePair> formValues = new ArrayList<>();
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            formValues.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
        }

        UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(formValues, StandardCharsets.UTF_8);
        urlEncodedFormEntity.setContentType("application/x-www-form-urlencoded");

        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(urlEncodedFormEntity);
        httpPost.setHeaders(convertMap2Headers(headers));

        return getRequestResult(httpPost);
    }

    /**
     * 下载文件
     *
     * @param url
     * @param paramMap
     * @return
     * @throws Exception
     */
    public static InputStream downloadFile(String url, Map<String, Object> paramMap) throws IOException {

        url = getGetUrl(url, paramMap);

        CloseableHttpClient httpClient = HttpClients.createDefault();

        RequestConfig timeoutConfig = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(1000)
                .setSocketTimeout(5000)
                .build();

        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(timeoutConfig);

        HttpResponse httpResponse = httpClient.execute(httpGet);

        if (httpResponse.getStatusLine().getStatusCode() != 200) {
            log.error("下载文件失败");
            return null;
        }

        return httpResponse.getEntity().getContent();
    }

    //map转header数组
    private static Header[] convertMap2Headers(Map<String, String> headerMap) {
        List<Header> headerList = new ArrayList<>();

        for (Map.Entry<String, String> entry : headerMap.entrySet()) {
            Header header = new BasicHeader(entry.getKey(), entry.getValue());
            headerList.add(header);
        }

        return headerList.toArray(new Header[headerList.size()]);
    }

    /**
     * get请求拼接参数
     *
     * @param url
     * @param paramMap
     * @return
     */
    private static String getGetUrl(String url, Map<String, Object> paramMap) {
        paramMap = paramMap == null ? new HashMap<>() : paramMap;
        if (!paramMap.isEmpty()) {
            url += "?" + formatUrlParam(paramMap);
        }

        return url;
    }

    /**
     * httpEntity -> String
     *
     * @param entity
     * @return
     * @throws IOException
     */
    private static String entityToString(HttpEntity entity) throws IOException {
        String result = null;

        if (entity != null) {
            long length = entity.getContentLength();

            if (length != -1 && length < 2048) {
                result = EntityUtils.toString(entity, UTF8);
            } else {

                InputStreamReader reader = new InputStreamReader(entity.getContent(), UTF8);
                CharArrayBuffer buffer = new CharArrayBuffer(2048);
                char[] tmp = new char[1024];

                int l;
                while ((l = reader.read(tmp)) != -1) {
                    buffer.append(tmp, 0, l);
                }

                result = buffer.toString();
            }
        }

        return result;
    }
}