package com.yonyou.dataswitch.base.auth.network;


import com.yonyou.dataswitch.base.exception.BusinessException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

/**
 * 本类主要用于Openapi组户级自建应用获取AccessToken的签名
 *
 * @author yonyou
 */
public class OpenApiRequestEncrypt {

    private static final String ALGORITHM = "HmacSHA256";

    public String signature(Map<String, String> params, String appSecret) {
        String source = buildSource(params);
        byte[] bytes;
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            SecretKeySpec secretKey = new SecretKeySpec(appSecret.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            mac.init(secretKey);
            bytes = mac.doFinal(source.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new BusinessException("exception when do open api token request signature", e);
        }

        String hash = Base64.getEncoder().encodeToString(bytes);
        try {
            return URLEncoder.encode(hash, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new BusinessException("implementation error, utf-8 not found, What year is it?");
        }
    }

    private String buildSource(Map<String, String> params) {
        Map<String, String> treeMap;
        if (params instanceof TreeMap) {
            treeMap = params;
        } else {
            treeMap = new TreeMap<>(params);
        }
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : treeMap.entrySet()) {
            builder.append(entry.getKey()).append(entry.getValue());
        }
        return builder.toString();
    }
}
