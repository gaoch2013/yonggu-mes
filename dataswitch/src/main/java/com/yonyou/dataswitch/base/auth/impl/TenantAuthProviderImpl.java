package com.yonyou.dataswitch.base.auth.impl;


import com.yonyou.dataswitch.base.auth.TenantAuthProvider;
import com.yonyou.dataswitch.base.auth.network.OpenApiRequestEncrypt;
import com.yonyou.dataswitch.base.auth.pojo.OpenApiAccessTokenResponse;
import com.yonyou.dataswitch.base.auth.token.OpenApiTokenCacheProvider;
import com.yonyou.dataswitch.base.datacenter.DataCenterUrlProvider;
import com.yonyou.dataswitch.base.exception.BusinessException;
import com.yonyou.dataswitch.base.properties.OpenApiProperties;
import com.yonyou.dataswitch.base.response.OpenApiAccessToken;
import com.yonyou.dataswitch.base.utils.RequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * 加密实现
 * @author yonyou
 */
@Slf4j
@RequiredArgsConstructor
public class TenantAuthProviderImpl implements TenantAuthProvider {

    private static final String URL_TOKEN = "open-auth/selfAppAuth/base/v1/getAccessToken?signature=%s&appKey=%s&timestamp=%s";

    private final OpenApiProperties properties;
    private final OpenApiRequestEncrypt encrypt;

    @Resource
    OpenApiTokenCacheProvider cacheProvider;

    @Resource
    DataCenterUrlProvider dataCenterUrlProvider;

    @SneakyThrows
    @Override
    public OpenApiAccessToken buildTenantAuthToken() {
        OpenApiAccessToken token = cacheProvider.loadTokenFromCache();
        //gaoch: 如果缓存中的token过期时间>10分钟，则直接返回此token，否则调用接口获取新的token
        if (token != null && (token.getExpiredAt() - System.currentTimeMillis()) > 1000*60*10) {
            return token;
        }
        return buildTenantAuthTokenFromRemote();

    }

    public OpenApiAccessToken buildTenantAuthTokenFromRemote() throws IOException {

        long timestamp = System.currentTimeMillis();
        String appKey = properties.getAppKey();
        Map<String, String> params = new TreeMap<>();
        params.put("appKey", appKey);
        params.put("timestamp", String.valueOf(timestamp));
        String signature = encrypt.signature(params, properties.getAppSecret());

        String url = properties.concatURL(dataCenterUrlProvider.buildTokenUrl(), String.format(URL_TOKEN, signature, appKey, timestamp));
        OpenApiAccessTokenResponse body = RequestUtil.doGet(url,null,OpenApiAccessTokenResponse.class);
        if (body != null) {
            body.check();
            OpenApiAccessToken accessToken = body.getData().build();
            cacheProvider.saveTokenToCache(accessToken);
            return accessToken;
        }

        throw new BusinessException("Unexpected empty response encountered when request open api access token");
    }

}
