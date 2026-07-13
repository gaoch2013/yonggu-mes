package com.yonyou.dataswitch.controller;

import com.yonyou.dataswitch.base.response.ApiDataPageResponse;
import com.yonyou.dataswitch.base.utils.JacksonUtils;
import com.yonyou.dataswitch.controller.util.ParamUtil;
import com.yonyou.dataswitch.service.RoutingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对外接口：工艺路线相关controller
 */
@Slf4j
@ResponseBody
@RestController
@RequestMapping("/dev/routing/v1")
public class RoutingController {

    @Resource
    RoutingService routingService;

    /**
     * @param inputParams 查询参数
     * @return 返回success表示成功接收，抛出异常等其他代表接收失败
     */
    @PostMapping("/getRoutingInfo")
    public String getRoutingInfo(@RequestBody Map<String,Object> inputParams) throws Exception {
        //请求地址：http://localhost:8833/dev/routing/v1/getRoutingInfo
        //body:
        log.info("getRoutingInfo--inputParams:{}",JacksonUtils.toJSONString(inputParams));
        String status = "success";
        String errorMsg = "";
        List<Map<String, Object>> result = new ArrayList<>();
        int allCount = 0;
        int retcount = 0;
        try {
            //先查列表数据
            Map<String, Object> qryParams = getQryParams(inputParams);
            ApiDataPageResponse pageResponse = routingService.list(qryParams);
            log.info("getRoutingInfo--listresult:{}",JacksonUtils.toJSONString(pageResponse));
            //再查详情数据：一条一条查询
             if(null!=pageResponse && !CollectionUtils.isEmpty(pageResponse.getRecordList())){
                 allCount = pageResponse.getRecordCount();
                 retcount = pageResponse.getRecordList().size();
                 List<Map<String,Object>> allDetails = new ArrayList<>();
                 for(Map<String, Object> tmpresult : pageResponse.getRecordList()){
                     Map<String,String> map = new HashMap<>();
                     map.put("id",tmpresult.get("id").toString());
                     Map<String, Object> detail =  routingService.detail(map);
                     if(!CollectionUtils.isEmpty(detail)){
                         allDetails.add(detail);
                     }
                 }
                 result = allDetails;
             }
        }catch (Exception e){
            log.error("getRoutingInfo error:{}",e.getMessage(),e);
            errorMsg = e.getMessage();
            status = "error";
        }
        Map<String, Object> resultMap = ParamUtil.formatOutputParam(allCount, retcount,"routing", result, status, errorMsg);

        log.info("getRoutingInfo--resultMap:{}",JacksonUtils.toJSONString(resultMap));
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
        qryParams.put("pageSize", inputParams.getOrDefault("page_size", 100));
        if(inputParams.containsKey("code")){
            qryParams.put("code", inputParams.get("code"));
        }
        if(inputParams.containsKey("productId")){
            qryParams.put("productId", inputParams.get("productId"));
        }
        //按物料编码查时只能传数组
        if(inputParams.containsKey("invcode")) {
            if (inputParams.get("invcode") != null && !"".equals(inputParams.get("invcode"))) {
                String[] productCodeList = inputParams.get("invcode").toString().split(",");
                qryParams.put("productCodes", productCodeList);
            }
        }
        qryParams.put("isSum",true);//固定条件
        //qryParams.put("status","1");//查已审核的工艺路线
        qryParams.put("versionScope","1");//版本范围：1-最新版本
        //其他传入的字段待补充 TODO...
        return qryParams;
    }

}