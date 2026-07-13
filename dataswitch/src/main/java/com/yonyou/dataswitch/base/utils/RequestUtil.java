package com.yonyou.dataswitch.base.utils;

import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.util.Map;

public class RequestUtil {

    private static final String HEADER_CONTENT_JSON = "application/json";

    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final Logger log = LoggerFactory.getLogger(RequestUtil.class);

    private static PoolingHttpClientConnectionManager cm = null;

    private static CloseableHttpClient httpClient;

    /**
     * 记录开放平台请求结果
     */
    public static class Response {
        /**
         * 该请求的 http 状态码
         * 200 为正常的返回结果
         */
        private int status;

        /**
         * 请求返回消息
         * 当 status == 200 时会返回 response body 中的字符串
         * 当 status !== 200 时会返回具体的错误信息
         */
        private String result;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }
    }

    static{
        cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(500);
        cm.setDefaultMaxPerRoute(50);

        RequestConfig globalConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(10000)         // 连接池获取连接超时
                .setConnectTimeout(10000)                   // 连接建立超时
                .setSocketTimeout(30000)                    // 等待响应超时
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                .build();

        httpClient = HttpClients.custom().setConnectionManager(cm).setDefaultRequestConfig(globalConfig).build();
    }

    private static CloseableHttpClient getHttpClient(){
        return httpClient;
    }

    public static <T> T doGet(String requestUrl, Map<String, String> paramMap, Class<T> type) throws IOException {
        return JacksonUtils.toJavaObject(doGet(requestUrl, paramMap), type);
    }

    public static <T> T doGetType(String requestUrl, Class<T> type) throws IOException {
        return JacksonUtils.toJavaObject(doGet(requestUrl, null), type);
    }

    public static <T> T doPost(String requestUrl, Object params, Class<T> type) throws IOException {
        return JacksonUtils.toJavaObject(doPost(requestUrl, params), type);
    }

    public static String doGet(String requestUrl, Map<String, String> paramMap) throws IOException {
        CloseableHttpClient httpClient = getHttpClient();
        StringBuilder param = new StringBuilder();
        if (paramMap != null) {
            if(!requestUrl.contains("?")){
                param.append("?");
            }else{
                param.append("&");
            }
            for(Map.Entry<String, String> entry: paramMap.entrySet()) {
                param.append(entry.getKey());
                param.append("=");
                param.append(entry.getValue());
                param.append("&");
            }
            param.deleteCharAt(param.length() - 1);
        }

        HttpGet get = new HttpGet(requestUrl + param);
        log.info("requestUrl: {}", requestUrl + param);
        String responseString = httpClient.execute(get, response -> EntityUtils.toString(response.getEntity()));
        get.releaseConnection();
        log.info("responseString: {}", responseString);
        return responseString;
    }


    public static String doPost(String requestUrl, Object params) throws IOException {
        CloseableHttpClient httpClient = getHttpClient();
        HttpPost post = new HttpPost(requestUrl);
        String paramsStr = JacksonUtils.toJSONString(params);
        log.info("requestUrl: {}", requestUrl);
        log.info("params: {}", paramsStr);
        assert paramsStr != null;
        StringEntity stringEntity = new StringEntity(paramsStr, ContentType.APPLICATION_JSON);
        post.setEntity(stringEntity);
        String responseString = httpClient.execute(post, response -> EntityUtils.toString(response.getEntity()));
        post.releaseConnection();
        log.info("responseString: {}", responseString);
        return responseString;
    }

}

