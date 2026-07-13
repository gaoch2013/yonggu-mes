package com.yonyou.dataswitch.service;

import com.yonyou.dataswitch.BaseOpenApi;
import com.yonyou.dataswitch.OpenApiURL;
import com.yonyou.dataswitch.base.response.ApiDataArrayResponse;
import com.yonyou.dataswitch.base.response.ApiDataPageResponse;
import com.yonyou.dataswitch.base.response.ApiDataResponse;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 计量单位相关接口
 */
@Component
public class UnitService extends BaseOpenApi {

//    /**
//     * 计量单位保存
//     * @param params
//     * @return 计量单位信息
//     */
//    public Map<String, Object> unitSave(Object params) {
//        ApiDataResponse apiDataResponse =  postForEntity(OpenApiURL.UNIT_SAVE,params, ApiDataResponse.class);
//        return getData(apiDataResponse);
//    }

    /**
     * 分页查询计量单位列表
     * @param params
     * @return 计量单位列表分页结果
     */
    public ApiDataPageResponse list(Map<String,Object> params) {
        //返回ApiDataPageResponse对象
        ApiDataArrayResponse apiDataResponse =  postForEntity(OpenApiURL.UNIT_LIST,params, ApiDataArrayResponse.class);
        return getData(apiDataResponse);
    }

    /**
     * 查询计量单位详情
     * @param params
     * @return 计量单位详情
     */
    public Map<String, Object> detail(Map<String,String> params) {
        ApiDataResponse apiDataResponse = getForEntity(OpenApiURL.UNIT_DETAIL,params, ApiDataResponse.class);
        return getData(apiDataResponse);
    }



}
