package com.yonyou.dataswitch.controller;

import com.yonyou.dataswitch.base.utils.JacksonUtils;
import com.yonyou.dataswitch.controller.util.ParamUtil;
import com.yonyou.dataswitch.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对外接口：库存相关controller
 */
@Slf4j
@ResponseBody
@RestController
@RequestMapping("/dev/stock/v1")
public class StockController {

    @Resource
    StockService stockService;

    /**
     * 现存量查询
     * @param inputParams 查询参数
     * @return 返回success表示成功接收，抛出异常等其他代表接收失败
     */
    @PostMapping("/getStockInfo")
    public String getStockInfo(@RequestBody Map<String,Object> inputParams) throws Exception {
        //请求地址：http://localhost:8833/dev/stock/v1/getStockInfo
        //body:
        log.info("getStockInfo--inputParams:{}",JacksonUtils.toJSONString(inputParams));
        String status = "success";
        String errorMsg = "";
        List<Map<String, Object>> result = new ArrayList<>();
        int allCount = 0;
        int retcount = 0;
        try {
            //先查列表数据
            Map<String, Object> qryParams = getStockQryParams(inputParams);
            List<Map<String, Object>> stockList = stockService.stockQuery(qryParams);
            log.info("getStockInfo--listresult:{}",JacksonUtils.toJSONString(stockList));
            if(null!=stockList && !CollectionUtils.isEmpty(stockList)){
                allCount = stockList.size();
                retcount = stockList.size();
                List<Map<String,Object>> allDetails = new ArrayList<>();
                for(Map<String, Object> detail : stockList){
                    if(!CollectionUtils.isEmpty(detail)){
                        detail = ParamUtil.formatSingleData("stockInfo", detail);
                        allDetails.add(detail);
                    }
                }
                result = allDetails;
            }
        }catch (Exception e){
            log.error("getStockInfo error:{}",e.getMessage(),e);
            errorMsg = e.getMessage();
            status = "error";
        }
        Map<String, Object> resultMap = ParamUtil.formatOutputParam(allCount, retcount,"stockInfo", result, status, errorMsg);

        log.info("getStockInfo--resultMap:{}",JacksonUtils.toJSONString(resultMap));
        return JacksonUtils.toJSONString(resultMap);
    }

    /**
     * 构造现存量查询参数
     * @param params
     * @return
     */
    private static Map<String, Object> getStockQryParams(Map<String, Object> params) throws Exception {
        Map<String,Object> inputParams = ParamUtil.getindata(params);
        //根据外系统传参构造列表查询参数
        Map<String,Object> qryParams = new HashMap<>();
        //库存组织
        if(inputParams.containsKey("orgCode")){
            qryParams.put("org.code", inputParams.getOrDefault("orgCode", "01"));
        }
        if(inputParams.containsKey("inventory")){
            if(inputParams.get("inventory") != null && !"".equals(inputParams.get("inventory"))) {
                if(inputParams.get("inventory").toString().contains(",")) {
                    String[] productCodeList = inputParams.get("inventory").toString().split(",");
                    qryParams.put("productn.code", productCodeList);
                }else{
                    qryParams.put("productn.code", inputParams.get("inventory"));
                }
            }
        }
        if(inputParams.containsKey("warehouse")){
            qryParams.put("warehouse.code", inputParams.get("warehouse"));
        }
        if(inputParams.containsKey("batchcode")){
            qryParams.put("batchno", inputParams.get("batchcode"));
        }
        if(inputParams.containsKey("location")){
            qryParams.put("location.code", inputParams.get("location"));
        }
        return qryParams;
    }

    /**
     * 货位现存量查询
     * @param inputParams 查询参数
     * @return 返回success表示成功接收，抛出异常等其他代表接收失败
     */
    @PostMapping("/getStockLocationInfo")
    public String getStockLocationInfo(@RequestBody Map<String,Object> inputParams) throws Exception {
        //请求地址：http://localhost:8833/dev/stock/v1/getStockLocationInfo
        //body:
        log.info("getStockLocationInfo--inputParams:{}",JacksonUtils.toJSONString(inputParams));
        String status = "success";
        String errorMsg = null;
        List<Map<String, Object>> result = null;
        int allCount = 0;
        int retcount = 0;
        try {
            //先查列表数据
            Map<String, Object> qryParams = getStockQryParams(inputParams);
            List<Map<String, Object>> stockList = stockService.stockLocationQuery(qryParams);
            log.info("getStockLocationInfo--listresult:{}",JacksonUtils.toJSONString(stockList));
            if(null!=stockList && !CollectionUtils.isEmpty(stockList)){
                allCount = stockList.size();
                retcount = stockList.size();
                List<Map<String,Object>> allDetails = new ArrayList<>();
                for(Map<String, Object> detail : stockList){
                    if(!CollectionUtils.isEmpty(detail)){
                        detail = ParamUtil.formatSingleData("stockLocation", detail);
                        allDetails.add(detail);
                    }
                }
                result = allDetails;
            }
        }catch (Exception e){
            log.error("getStockLocationInfo error:{}",e.getMessage(),e);
            errorMsg = e.getMessage();
            status = "error";
        }
        Map<String, Object> resultMap = ParamUtil.formatOutputParam(allCount, retcount,"stockLocation", result, status, errorMsg);

        log.info("getStockLocationInfo--resultMap:{}",JacksonUtils.toJSONString(resultMap));
        return JacksonUtils.toJSONString(resultMap);
    }
}