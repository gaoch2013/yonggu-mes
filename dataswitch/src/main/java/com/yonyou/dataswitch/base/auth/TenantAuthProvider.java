package com.yonyou.dataswitch.base.auth;


import com.yonyou.dataswitch.base.response.OpenApiAccessToken;

/**
 * 本类主要用于租户自建应用授权
 */
public interface TenantAuthProvider{

    OpenApiAccessToken buildTenantAuthToken();

}
