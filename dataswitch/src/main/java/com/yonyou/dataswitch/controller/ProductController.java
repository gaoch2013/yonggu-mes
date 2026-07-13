package com.yonyou.dataswitch.controller;

import com.yonyou.dataswitch.base.response.ApiDataPageResponse;
import com.yonyou.dataswitch.base.utils.JacksonUtils;
import com.yonyou.dataswitch.controller.util.ParamUtil;
import com.yonyou.dataswitch.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对外接口：产品相关controller
 */
@Slf4j
@ResponseBody
@RestController
@RequestMapping("/dev/product/v1")
public class ProductController {

    @Resource
    ProductService productService;

    /**
     * @param inputParams 查询参数
     * @return 返回success表示成功接收，抛出异常等其他代表接收失败
     */
    @PostMapping("/getProductInfo")
    public String getProductInfo(@RequestBody Map<String,Object> inputParams) throws Exception {
        //请求地址：http://localhost:8833/dev/product/v1/getProductInfo
        //body:
        log.info("getProductInfo--inputParams:{}",JacksonUtils.toJSONString(inputParams));
        String status = "success";
        String errorMsg = "";
        List<Map<String, Object>> result = new ArrayList<>();
        int allCount = 0;
        int retcount = 0;
        try {
            //先查列表数据
            Map<String, Object> qryParams = getQryParams(inputParams);
            ApiDataPageResponse pageResponse = productService.list(qryParams);
            log.info("getProductInfo--listresult:{}",JacksonUtils.toJSONString(pageResponse));
            //再查详情数据：批量详情查询最多支持10条数据
             if(null != pageResponse && !CollectionUtils.isEmpty(pageResponse.getRecordList())){
                 allCount = pageResponse.getRecordCount();
                 retcount = pageResponse.getRecordList().size();
                 List<Map<String,Object>> mapList = new ArrayList<>();
                 for(Map<String, Object> tmpresult : pageResponse.getRecordList()){
                     Map<String,Object> map = new HashMap<>();
                     map.put("orgId",tmpresult.get("createOrgId").toString());
                     map.put("id",tmpresult.get("id").toString());
                     mapList.add(map);
                 }
                 //按每10条分批调用details
                 int totalCount = mapList.size();
                 int batchSize = 10;
                 List<Map<String,Object>> allDetails = new ArrayList<>();
                 for(int i = 0; i < totalCount; i += batchSize){
                     int end = Math.min(i + batchSize, totalCount);
                     List<Map<String,Object>> batch = mapList.subList(i, end);
                     List<Map<String, Object>> batchResult = productService.details(batch);
                     if(!CollectionUtils.isEmpty(batchResult)){
                         for(int j = 0; j < batchResult.size(); j++){
                             Map<String,Object> detail = batchResult.get(j);
                             detail = ParamUtil.formatSingleData("product", detail);
                             allDetails.add(detail);
                         }
                     }
                 }
                 result = allDetails;
             }
        }catch (Exception e){
            log.error("getProductInfo error:{}",e.getMessage(),e);
            errorMsg = e.getMessage();
            status = "error";
        }
        Map<String, Object> resultMap = ParamUtil.formatOutputParam(allCount, retcount,"product", result, status, errorMsg);

        log.info("getProductInfo--resultMap:{}",JacksonUtils.toJSONString(resultMap));
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
        qryParams.put("isSum", true);//固定条件
        //库存组织 bodycode 01
        //物料编码
        if(inputParams.containsKey("invcode")){
            if(inputParams.get("invcode") != null && !"".equals(inputParams.get("invcode"))) {
                if(inputParams.get("invcode").toString().contains(",")) {
                    String[] productCodeList = inputParams.get("invcode").toString().split(",");
                    qryParams.put("productCodeList", productCodeList);
                }else{
                    qryParams.put("productCode", inputParams.get("invcode"));
                }
            }
        }
        return qryParams;
    }
}