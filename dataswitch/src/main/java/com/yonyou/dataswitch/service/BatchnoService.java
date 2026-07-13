package com.yonyou.dataswitch.service;

import com.yonyou.dataswitch.BaseOpenApi;
import com.yonyou.dataswitch.OpenApiURL;
import com.yonyou.dataswitch.base.response.*;
import com.yonyou.dataswitch.base.utils.RequestUtil;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 批次号相关接口
 */
@Component
public class BatchnoService extends BaseOpenApi {

    /**
     * 批次号列表
     * @param params
     * @return 批次号
     */
    public String list(Map<String,Object> params) throws IOException {
//        ApiDataStringResponse apiDataResponse =  postForEntity(OpenApiURL.BATCHNO_LIST_QUERY,params, ApiDataStringResponse.class);
//        return getData(apiDataResponse);
        return RequestUtil.doPost(buildTokenRequestUrl(OpenApiURL.BATCHNO_LIST_QUERY),params);
    }


}
