package com.yonyou.dataswitch.service;

import com.yonyou.dataswitch.BaseOpenApi;
import com.yonyou.dataswitch.OpenApiURL;
import com.yonyou.dataswitch.base.response.ApiDataArrayResponse;
import com.yonyou.dataswitch.base.response.ApiDataPageResponse;
import com.yonyou.dataswitch.base.response.ApiDataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 生产订单相关接口
 */
@Component
public class ProductionOrderService extends BaseOpenApi {
    @Autowired
    private UnitService unitService;

    /**
     * 分页查询生产订单列表
     * @param params
     * @return 生产订单列表分页结果
     */
    public ApiDataPageResponse list(Map<String,Object> params) {
        //返回ApiDataPageResponse对象
        ApiDataArrayResponse apiDataResponse =  postForEntity(OpenApiURL.PRODUCTION_ORDER_QUERY,params, ApiDataArrayResponse.class);
        return getData(apiDataResponse);
    }
    /**
     * 分页查询生产订单列表
     * @param params
     * @return 生产订单列表
     */
    public List<Map<String, Object>> listEx(Map<String,Object> params) {
        //列表查询返回结果带了pageindex和pagesize，所以此处返回的是一个列表
        ApiDataArrayResponse apiDataResponse =  postForEntity(OpenApiURL.PRODUCTION_ORDER_QUERY,params, ApiDataArrayResponse.class);
        return getData(apiDataResponse).getRecordList();
    }
    /**
     * 查询生产订单详情
     * @param params
     * @return 生产订单详情
     */
    public Map<String, Object> detail(Map<String,String> params) {
        ApiDataResponse apiDataResponse = getForEntity(OpenApiURL.PRODUCTION_ORDER_DETAIL,params, ApiDataResponse.class);
        Map<String, Object> tempresult = getData(apiDataResponse);
        //fillBodyUnitCode(tempresult);
        return tempresult;
    }
    /**
     * 生产订单完工
     * @param params
     * @return 保存结果
     */
    public Map<String, Object> batchfinishWork(Object params) {
        ApiDataResponse apiDataResponse = postForEntity(OpenApiURL.PRODUCTION_ORDER_FINISH_WORK, params, ApiDataResponse.class);
        return getData(apiDataResponse);
    }
    private void fillBodyUnitCode(Map<String, Object> result) {
        // 根据计量单位id获取计量单位编码
        if (result != null && "yonggu".equals(properties.getTenantCode())) {
            if(result.containsKey("orderProduct")) {
                List<Map<String, Object>> list = (List<Map<String, Object>>) result.get("orderProduct");
                if (!CollectionUtils.isEmpty(list)) {
                    Map<String, String> qryUnitmap = new HashMap<>();
                    for (Map<String, Object> item : list) {
                        qryUnitmap = new HashMap<>();
                        qryUnitmap.put("id", item.get("mainUnit").toString());
                        Map<String, Object> unitDetail = unitService.detail(qryUnitmap);
                        if (!CollectionUtils.isEmpty(unitDetail)) {
                            item.put("mainUnitCode", unitDetail.get("code").toString());
                        }
                        qryUnitmap = new HashMap<>();
                        qryUnitmap.put("id", item.get("bomUnitId").toString());
                        unitDetail = unitService.detail(qryUnitmap);
                        if (!CollectionUtils.isEmpty(unitDetail)) {
                            item.put("bomUnitCode", unitDetail.get("code").toString());
                        }
                    }
                }
            }
        }
    }
}
