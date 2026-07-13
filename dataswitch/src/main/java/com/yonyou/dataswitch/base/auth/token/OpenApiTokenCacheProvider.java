package com.yonyou.dataswitch.base.auth.token;


import com.yonyou.dataswitch.base.response.OpenApiAccessToken;

/**
 * @author yonyou
 * @description: 用于token缓存处理
 */
public interface OpenApiTokenCacheProvider {

    OpenApiAccessToken loadTokenFromCache();

    void saveTokenToCache(OpenApiAccessToken token);
}
