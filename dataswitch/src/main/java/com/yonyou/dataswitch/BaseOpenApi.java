package com.yonyou.dataswitch;

import com.yonyou.dataswitch.base.auth.TenantAuthProvider;
import com.yonyou.dataswitch.base.datacenter.DataCenterUrlProvider;
import com.yonyou.dataswitch.base.exception.BusinessException;
import com.yonyou.dataswitch.base.properties.OpenApiProperties;
import com.yonyou.dataswitch.base.response.OpenApiAccessToken;
import com.yonyou.dataswitch.base.response.OpenApiResponse;
import com.yonyou.dataswitch.base.utils.ReSubmitUtil;
import com.yonyou.dataswitch.base.utils.RequestUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: 开放平台接口调用工具类
 **/
@Slf4j
@Component
public class BaseOpenApi {

    @Resource
    TenantAuthProvider tenantAuthProvider;
    @Resource
    @Getter
    protected OpenApiProperties properties;
    @Resource
    public DataCenterUrlProvider dataCenterUrlProvider;

    @SneakyThrows
    public <T> T getForEntity(String urlFragment, Map<String,String> params,Class<T> responseType) {
        return  RequestUtil.doGet(buildTokenRequestUrl(urlFragment),params, responseType);
    }

    @SneakyThrows
    public <T> T postForEntity(String urlFragment, Object params,Class<T> responseType) {
        return RequestUtil.doPost(buildTokenRequestUrl(urlFragment),params,responseType);
    }

    public <T> T postForEntityReSubmit(String urlFragment, Object params,Class<T> responseType) {
        ReSubmitUtil.resubmitCheckKey(params);
        return postForEntity(urlFragment, params,responseType);
    }


    public <T> T postForEntityReSubmit(String urlFragment, Object params,Class<T> responseType,String dateFormat) {
        ReSubmitUtil.resubmitCheckKey(params, dateFormat);
        return postForEntity(urlFragment, params,responseType);
    }




    @SneakyThrows
    public <T> T getData(OpenApiResponse<T> body){
        if (body == null) {
            throw new BusinessException("unexpected response null when request open api isv access token");
        }
        body.check();
        return body.getData();
    }

    protected String buildTokenRequestUrl(String urlFragment) {
        String host = dataCenterUrlProvider.buildBusinessUrl();
        Map<String,String> params = new HashMap<String, String>();
        params.put("access_token", getAccessToken().getAccessToken());
        String url = properties.concatParam(host+urlFragment,  properties.buildQueryString(params, false));
        return url;
    }

    protected OpenApiAccessToken getAccessToken(){
        return tenantAuthProvider.buildTenantAuthToken();
    }

    protected Map<String,Object> dataMap(Object object){
        Map<String,Object> map = new HashMap<>();
        map.put("data",object);
        return  map;
    }


}
