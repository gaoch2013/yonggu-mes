package com.yonyou.dataswitch.service;

import com.yonyou.dataswitch.BaseOpenApi;
import com.yonyou.dataswitch.OpenApiURL;
import com.yonyou.dataswitch.base.response.ApiDataListResponse;
import com.yonyou.dataswitch.base.response.ApiDataResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 组织相关接口
 */
@Component
public class OrgService extends BaseOpenApi {

//    /**
//     * 组织保存
//     * @param params
//     * @return 组织保存结果
//     */
//    public Map<String, Object> save(Object params) {
//        ApiDataResponse apiDataResponse = postForEntity(OpenApiURL.ORG_SAVE, params, ApiDataResponse.class);
//        return getData(apiDataResponse);
//    }

    public List<Map<String, Object>> list(Map<String,Object> params) {
        ApiDataListResponse apiDataResponse =  postForEntity(OpenApiURL.ORG_LIST,params, ApiDataListResponse.class);
        return getData(apiDataResponse);
    }

    public Map<String, Object> detail(Map<String,String> params) {
        ApiDataResponse apiDataResponse = getForEntity(OpenApiURL.ORG_DETAIL,params, ApiDataResponse.class);
        return getData(apiDataResponse);
    }

}
