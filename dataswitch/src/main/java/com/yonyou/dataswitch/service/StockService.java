package com.yonyou.dataswitch.service;

import com.yonyou.dataswitch.BaseOpenApi;
import com.yonyou.dataswitch.OpenApiURL;
import com.yonyou.dataswitch.base.response.ApiDataListResponse;
import com.yonyou.dataswitch.base.response.ApiDataResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 存量相关接口
 */
@Component
public class StockService extends BaseOpenApi {

//    /**
//     * 现存量保存
//     * @param params
//     * @return 现存量信息
//     */
//    public Map<String, Object> stockSave(Object params) {
//        ApiDataResponse apiDataResponse =  postForEntity(OpenApiURL.STOCK_SAVE,params, ApiDataResponse.class);
//        return getData(apiDataResponse);
//    }

    /**
     * 现存量查询
     * @param params
     * @return 现存量信息
     */
    public List<Map<String, Object>> stockQuery(Map<String,Object> params) {
        //此处返回的是一个列表，返回类型应该是 ApiDataListResponse
        ApiDataListResponse apiDataResponse = postForEntity(OpenApiURL.STOCK_QUERY,params, ApiDataListResponse.class);
        return getData(apiDataResponse);
    }

    /**
     * 货位现存量保存
     * @param params
     * @return 货位现存量信息
     */
    public Map<String, Object> stockLocationSave(Object params) {
        ApiDataResponse apiDataResponse =  postForEntity(OpenApiURL.STOCK_LOCATION_SAVE,params, ApiDataResponse.class);
        return getData(apiDataResponse);
    }

    /**
     * 货位现存量查询
     * @param params
     * @return 货位现存量信息
     */
    public List<Map<String, Object>> stockLocationQuery(Map<String,Object> params) {
        //此处返回的是一个列表，返回类型应该是 ApiDataListResponse
        ApiDataListResponse apiDataResponse = postForEntity(OpenApiURL.STOCK_LOCATION_QUERY,params, ApiDataListResponse.class);
        return getData(apiDataResponse);
    }


}
