package com.yonyou.dataswitch.service;

import com.yonyou.dataswitch.BaseOpenApi;
import com.yonyou.dataswitch.OpenApiURL;
import com.yonyou.dataswitch.base.response.ApiDataArrayResponse;
import com.yonyou.dataswitch.base.response.ApiDataListResponse;
import com.yonyou.dataswitch.base.response.ApiDataPageResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 物料相关接口
 */
@Component
public class ProductService extends BaseOpenApi {

//    /**
//     * 物料保存
//     * @param params
//     * @return 物料保存结果
//     */
//    public Map<String, Object> save(Object params) {
//        ApiDataResponse apiDataResponse =  postForEntity(OpenApiURL.PRODUCT_SAVE,params, ApiDataResponse.class);
//        return getData(apiDataResponse);
//    }

    /**
     * 分页查询产品列表
     * @param params
     * @return 产品列表分页结果
     */
    public ApiDataPageResponse list(Map<String,Object> params) {
        //返回ApiDataPageResponse对象
        ApiDataArrayResponse apiDataResponse =  postForEntity(OpenApiURL.PRODUCT_LIST,params, ApiDataArrayResponse.class);
        return getData(apiDataResponse);
    }

    /**
     * 分页查询产品列表
     * @param params
     * @return 产品列表
     */
    public List<Map<String, Object>> listEx(Map<String,Object> params) {
        //列表查询返回结果带了pageindex和pagesize，所以此处返回的是一个列表
        ApiDataArrayResponse apiDataResponse =  postForEntity(OpenApiURL.PRODUCT_LIST,params, ApiDataArrayResponse.class);
        return getData(apiDataResponse).getRecordList();
    }
    /**
     * 查询产品详情
     * @param params
     * @return 产品详情
     */
    public List<Map<String, Object>> details(List<Map<String,Object>> params) {
        //详情查询是批量查询，所以此处返回的是一个列表，返回类型应该是 ApiDataListResponse
        ApiDataListResponse apiDataResponse = postForEntity(OpenApiURL.PRODUCT_DETAIL,params, ApiDataListResponse.class);
        return getData(apiDataResponse);
    }

}
