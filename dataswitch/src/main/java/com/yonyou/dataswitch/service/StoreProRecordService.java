package com.yonyou.dataswitch.service;

import com.yonyou.dataswitch.BaseOpenApi;
import com.yonyou.dataswitch.OpenApiURL;
import com.yonyou.dataswitch.base.response.ApiDataArrayResponse;
import com.yonyou.dataswitch.base.response.ApiDataPageResponse;
import com.yonyou.dataswitch.base.response.ApiDataResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 产品入库单相关接口
 */
@Component
public class StoreProRecordService extends BaseOpenApi {

    /**
     * 分页查询产品入库单列表
     * @param params
     * @return 产品入库单列表分页结果
     */
    public ApiDataPageResponse list(Map<String,Object> params) {
        //返回ApiDataPageResponse对象
        ApiDataArrayResponse apiDataResponse =  postForEntity(OpenApiURL.STORE_PRO_RECORD_QUERY,params, ApiDataArrayResponse.class);
        return getData(apiDataResponse);
    }
    /**
     * 分页查询产品入库单列表
     * @param params
     * @return 产品入库单列表
     */
    public List<Map<String, Object>> listEx(Map<String,Object> params) {
        //列表查询返回结果带了pageindex和pagesize，所以此处返回的是一个列表
        ApiDataArrayResponse apiDataResponse =  postForEntity(OpenApiURL.STORE_PRO_RECORD_QUERY,params, ApiDataArrayResponse.class);
        return getData(apiDataResponse).getRecordList();
    }
    /**
     * 查询产品入库单详情
     * @param params
     * @return 产品入库单详情
     */
    public Map<String, Object> detail(Map<String,String> params) {
        ApiDataResponse apiDataResponse = getForEntity(OpenApiURL.STORE_PRO_RECORD_DETAIL,params, ApiDataResponse.class);
        return getData(apiDataResponse);
    }
    /**
     * 保存产品入库单
     * @param params
     * @return 保存结果
     */
    public Map<String, Object> save(Object params) {
        ApiDataResponse apiDataResponse = postForEntity(OpenApiURL.STORE_PRO_RECORD_SAVE, params, ApiDataResponse.class);
        return getData(apiDataResponse);
    }
}
