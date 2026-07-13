package com.yonyou.dataswitch.controller;

import com.yonyou.dataswitch.base.response.ApiDataPageResponse;
import com.yonyou.dataswitch.base.utils.JacksonUtils;
import com.yonyou.dataswitch.controller.util.ParamUtil;
import com.yonyou.dataswitch.service.PickingRequisitionService;
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
 * 对外接口：出库申请单相关controller
 */
@Slf4j
@ResponseBody
@RestController
@RequestMapping("/dev/pickingRequisition/v1")
public class PickingRequisitionController {

    @Resource
    PickingRequisitionService pickingRequisitionService;
    @Resource
    ProductionOrderService productionOrderService;

    /**
     * @param inputParams 查询参数
     * @return 返回success表示成功接收，抛出异常等其他代表接收失败
     */
    @PostMapping("/getPickingRequisitionInfo")
    public String getPickingRequisitionInfo(@RequestBody Map<String,Object> inputParams) throws Exception {
        //请求地址：http://localhost:8833/dev/pickingRequisition/v1/getPickingRequisitionInfo
        //body:
        log.info("getPickingRequisitionInfo--inputParams:{}",JacksonUtils.toJSONString(inputParams));
        String status = "success";
        String errorMsg = "";
        List<Map<String, Object>> result = new ArrayList<>();
        int allCount = 0;
        int retcount = 0;
        try {
            //先查列表数据
            Map<String, Object> qryParams = getQryParams(inputParams);
            ApiDataPageResponse pageResponse = pickingRequisitionService.list(qryParams);
            log.info("getPickingRequisitionInfo--listresult:{}",JacksonUtils.toJSONString(pageResponse));
            //再查详情数据：一条一条查询
            if(null!=pageResponse && !CollectionUtils.isEmpty(pageResponse.getRecordList())){
                allCount = pageResponse.getRecordCount();
                retcount = pageResponse.getRecordList().size();
                List<Map<String,Object>> allDetails = new ArrayList<>();
                for(Map<String, Object> tmpresult : pageResponse.getRecordList()){
                    Map<String,String> map = new HashMap<>();
                    map.put("id",tmpresult.get("id").toString());
                    Map<String, Object> detail =  pickingRequisitionService.detail(map);
                    if(!CollectionUtils.isEmpty(detail)){
                        //只获取生产订单是已开工状态的数据
                        List<Map<String, Object>> scdddetails = (List<Map<String, Object>>) detail.get("requisitionDetail");
                        String scddid = scdddetails.get(0).get("sourceid").toString();
                        Map<String,String> scddmap = new HashMap<>();
                        scddmap.put("id",scddid);
                        Map<String, Object> scdddetail =  productionOrderService.detail(scddmap);
                        if(scdddetail.get("status").toString().equals("5")){
                            detail = ParamUtil.formatSingleData("pickingRequisition", detail);
                            allDetails.add(detail);
                        }else{
                            allCount--;
                            retcount--;
                        }
                    }
                }
                result = allDetails;
            }
        }catch (Exception e){
            log.error("getPickingRequisitionInfo error:{}",e.getMessage(),e);
            errorMsg = e.getMessage();
            status = "error";
        }
        Map<String, Object> resultMap = ParamUtil.formatOutputParam(allCount, retcount,"pickingRequisition", result, status, errorMsg);

        log.info("getPickingRequisitionInfo--resultMap:{}",JacksonUtils.toJSONString(resultMap));
        return JacksonUtils.toJSONString(resultMap);
    }

    /**
     * 构造查询参数
     * @param params
     * @return
     */
    private static Map<String, Object> getQryParams(Map<String, Object> params) throws Exception {
        Map<String,Object> inputParams = ParamUtil.getindata(params);
        //根据外系统传参构造列表查询参数
        Map<String,Object> qryParams = new HashMap<>();
        qryParams.put("pageIndex", inputParams.getOrDefault("page_now", 1));
        qryParams.put("pageSize", inputParams.getOrDefault("page_size", 200));
        qryParams.put("isSum", true);//固定条件
        qryParams.put("status", "1");//只查已审核的出库申请单
//        if(inputParams.containsKey("code") && inputParams.get("code") != null && !"".equals(inputParams.get("code").toString())){
//            qryParams.put("code", inputParams.get("code"));
//        }
        String startDate = null, endDate = null, vouchdate = null;
        if(inputParams.containsKey("date_begin") && inputParams.get("date_begin") != null && !"".equals(inputParams.get("date_begin").toString())){
            startDate = inputParams.get("date_begin").toString();
        }
        if(inputParams.containsKey("date_end") && inputParams.get("date_end") != null && !"".equals(inputParams.get("date_end").toString())){
            endDate = inputParams.get("date_end").toString();
        }
        if(null!=startDate){
            vouchdate = startDate;
            if(null!=endDate){
                vouchdate += "|" + endDate;
            }
            qryParams.put("vouchdate", vouchdate);
        }
        //列表查询的api无code的条件，需要拼接simplevo
        List<Map<String, Object>> simpleVOs = new ArrayList<>();
        if(inputParams.containsKey("code") && inputParams.get("code") != null) {
            Map<String, Object> simpleVO = new HashMap<>();
            simpleVO.put("field", "code");
            simpleVO.put("op", "eq");
            simpleVO.put("value1", inputParams.get("code"));
            simpleVOs.add(simpleVO);
        }
        qryParams.put("simpleVOs", simpleVOs);
        return qryParams;
    }
}