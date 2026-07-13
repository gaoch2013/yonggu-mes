package com.yonyou.dataswitch.controller;

import com.yonyou.dataswitch.base.response.ApiDataPageResponse;
import com.yonyou.dataswitch.base.utils.JacksonUtils;
import com.yonyou.dataswitch.controller.util.ParamUtil;
import com.yonyou.dataswitch.service.ProductionOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对外接口：生产订单相关controller
 */
@Slf4j
@ResponseBody
@RestController
@RequestMapping("/dev/productionOrder/v1")
public class ProductionOrderController {

    @Resource
    ProductionOrderService productionOrderService;


    /**
     * @param inputParams 查询参数
     * @return 返回success表示成功接收，抛出异常等其他代表接收失败
     */
    @PostMapping("/getProductionOrderInfo")
    public String getProductionOrderInfo(@RequestBody Map<String,Object> inputParams) throws Exception {
        //请求地址：http://localhost:8833/dev/productionOrder/v1/getProductionOrderInfo
        //body:
        log.info("getProductionOrderInfo--inputParams:{}",JacksonUtils.toJSONString(inputParams));
        String status = "success";
        String errorMsg = "";
        List<Map<String, Object>> result = new ArrayList<>();
        int allcount = 0;
        int retcount = 0;
        try {
            //先查列表数据
            Map<String, Object> qryParams = getQryParams(inputParams);
            ApiDataPageResponse pageResponse = productionOrderService.list(qryParams);
            log.info("getProductionOrderInfo--listresult:{}",JacksonUtils.toJSONString(pageResponse));
            //再查详情数据：一条一条查询
            if(null!=pageResponse && !CollectionUtils.isEmpty(pageResponse.getRecordList())){
                allcount = pageResponse.getRecordCount();
                retcount = pageResponse.getRecordList().size();
                List<Map<String,Object>> allDetails = new ArrayList<>();
                for(Map<String, Object> tmpresult : pageResponse.getRecordList()){
                    Map<String,String> map = new HashMap<>();
                    map.put("id",tmpresult.get("id").toString());
                    Map<String, Object> detail =  productionOrderService.detail(map);
                    if(!CollectionUtils.isEmpty(detail)){
                        detail = ParamUtil.formatSingleData("productionOrder", detail);
                        allDetails.add(detail);
                    }
                }
                result = allDetails;
            }
        }catch (Exception e){
            log.error("getProductionOrderInfo error:{}",e.getMessage(),e);
            errorMsg = e.getMessage();
            status = "error";
        }

        Map<String, Object> resultMap = ParamUtil.formatOutputParam(allcount, retcount, "productionOrder",result, status, errorMsg);

        log.info("getProductionOrderInfo--resultMap:{}",JacksonUtils.toJSONString(resultMap));
        return JacksonUtils.toJSONString(resultMap);
    }

    /**
     * @param inputParams 更新参数
     * @return 返回success表示成功接收，抛出异常等其他代表接收失败
     */
    @PostMapping("/batchfinishWork")
    public String batchfinishWork(@RequestBody Map<String,Object> inputParams) throws Exception {
        //请求地址：http://localhost:8833/dev/productionOrder/v1/batchfinishWork
        //body:
        log.info("batchfinishWork--inputParams:{}",JacksonUtils.toJSONString(inputParams));
        String status = "success";
        String errorMsg = "";
        List<Map<String, Object>> result = new ArrayList<>();
        int allcount = 0;
        int retcount = 0;
        try {
            //先查列表数据
            Map<String, Object> filishParams = getFilishParams(inputParams);
            Map<String, Object> saveResult = productionOrderService.batchfinishWork(filishParams);
            log.info("batchfinishWork--saveresult:{}",JacksonUtils.toJSONString(saveResult));
            Map<String, Object> rtnMap = JacksonUtils.toMap(saveResult);
            if(!CollectionUtils.isEmpty(rtnMap) && rtnMap.get("failCount")!=null && rtnMap.get("failCount").toString().equals("1")){
                throw new Exception(rtnMap.get("messages").toString());
            }
            result.add(saveResult);
        }catch (Exception e){
            log.error("batchfinishWork error:{}",e.getMessage(),e);
            errorMsg = e.getMessage();
            status = "error";
        }

        Map<String, Object> resultMap = ParamUtil.formatOutputParam(allcount, retcount, "updatPproductionOrder",result, status, errorMsg);

        log.info("batchfinishWork--resultMap:{}",JacksonUtils.toJSONString(resultMap));
        return JacksonUtils.toJSONString(resultMap);
    }

    /**
     * 构造查询参数
     * @param params
     * @return
     */
    private static Map<String, Object> getQryParams(Map<String, Object> params) throws Exception {
        Map<String, Object> inputParams = ParamUtil.getindata(params);
        //根据外系统传参构造列表查询参数
        Map<String, Object> qryParams = new HashMap<>();
        qryParams.put("pageIndex", inputParams.getOrDefault("page_now", 1));
        qryParams.put("pageSize", inputParams.getOrDefault("page_size", 200));
        qryParams.put("isSum", true);//固定条件
        qryParams.put("status", "5");//查已开工的订单 1 已审核 5 已开工

        //单号
        if (inputParams.containsKey("scddh") && inputParams.get("scddh") != null && !"".equals(inputParams.get("scddh").toString())) {
            qryParams.put("code", inputParams.get("scddh"));
        }
        //日期
        if (inputParams.containsKey("zdrq") && inputParams.get("zdrq") != null && !"".equals(inputParams.get("zdrq").toString())) {
            String date = inputParams.get("zdrq").toString().substring(0, 10);
            qryParams.put("vouchdate", date + "|" + date + " 23:59:59");
        }
        return qryParams;
    }

    private static Map<String, Object> getFilishParams(Map<String, Object> params) throws Exception {
        Map<String, Object> inputParams = ParamUtil.getindata(params);
        //根据外系统传参构造列表查询参数
        Map<String, Object> qryParams = new HashMap<>();
        if(inputParams.containsKey("mocode")){
            qryParams.put("code", inputParams.get("mocode"));
        }
        if(inputParams.containsKey("moid")){
            qryParams.put("id", inputParams.get("moid"));
        }
        List<Map<String, Object>> qryParamsList = new ArrayList<>();
        qryParamsList.add(qryParams);
        Map<String, Object> rtnParams = new HashMap<>();
        rtnParams.put("data", qryParamsList);
        return rtnParams;
    }
}