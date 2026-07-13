package com.yonyou.dataswitch.controller;

import com.yonyou.dataswitch.base.response.ApiDataPageResponse;
import com.yonyou.dataswitch.base.utils.JacksonUtils;
import com.yonyou.dataswitch.controller.util.ExchangeUtil;
import com.yonyou.dataswitch.controller.util.ParamUtil;
import com.yonyou.dataswitch.service.StoreProRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 对外接口：产品入库单相关controller
 */
@Slf4j
@ResponseBody
@RestController
@RequestMapping("/dev/storeProRecordController/v1")
public class StoreProRecordController {

    @Resource
    StoreProRecordService storeProRecordService;
    @Resource
    ProductionOrderController productionOrderController;
    /**
     * @param inputParams 查询参数
     * @return 返回success表示成功接收，抛出异常等其他代表接收失败
     */
    @PostMapping("/getStoreProRecordInfo")
    public String getStoreProRecordInfo(@RequestBody Map<String,Object> inputParams) throws Exception {
        //请求地址：http://localhost:8833/dev/storeProRecordController/v1/getStoreProRecordInfo
        //body:
        log.info("getStoreProRecordInfo--inputParams:{}",JacksonUtils.toJSONString(inputParams));
        String status = "success";
        String errorMsg = "";
        List<Map<String, Object>> result = new ArrayList<>();
        int allCount = 0;
        int retcount = 0;
        try {
            //先查列表数据
            Map<String, Object> qryParams = getQryParams(inputParams);
            ApiDataPageResponse pageResponse = storeProRecordService.list(qryParams);
            log.info("getStoreProRecordInfo--listresult:{}",JacksonUtils.toJSONString(pageResponse));
            //再查详情数据：一条一条查询
            if(null!=pageResponse && !CollectionUtils.isEmpty(pageResponse.getRecordList())){
                allCount = pageResponse.getRecordCount();
                retcount = pageResponse.getRecordList().size();
                List<Map<String,Object>> allDetails = new ArrayList<>();
                for(Map<String, Object> tmpresult : pageResponse.getRecordList()){
                    Map<String,String> map = new HashMap<>();
                    map.put("id",tmpresult.get("id").toString());
                    Map<String, Object> detail =  storeProRecordService.detail(map);
                    if(!CollectionUtils.isEmpty(detail)){
                        detail = ParamUtil.formatSingleData("storeProRecord", detail);
                        allDetails.add(detail);
                    }
                }
                result = allDetails;
            }
        }catch (Exception e){
            log.error("getStoreProRecordInfo error:{}",e.getMessage(),e);
            errorMsg = e.getMessage();
            status = "error";
        }
        Map<String, Object> resultMap = ParamUtil.formatOutputParam(allCount, retcount,"storeProRecord", result, status, errorMsg);

        log.info("getStoreProRecordInfo--resultMap:{}",JacksonUtils.toJSONString(resultMap));
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
        //按生产订单号查产品入库单
        List<Map<String, Object>> simpleVOs = new ArrayList<>();
        if(inputParams.containsKey("mocode") && inputParams.get("mocode") != null) {
            Map<String, Object> simpleVO = new HashMap<>();
            simpleVO.put("field", "storeProRecords.mocode");
            simpleVO.put("op", "eq");
            simpleVO.put("value1", inputParams.get("mocode"));
            simpleVOs.add(simpleVO);
        }
        qryParams.put("simpleVOs", simpleVOs);
        return qryParams;
    }

    /**
     * @param inputParams 保存参数
     * @return 返回success表示成功接收，抛出异常等其他代表接收失败
     */
    @PostMapping("/saveStoreProRecordInfo")
    public String saveStoreProRecordInfo(@RequestBody Map<String,Object> inputParams) throws Exception {
        //请求地址：http://localhost:8833/dev/storeProRecordController/v1/saveStoreProRecordInfo
        //body:
        log.info("saveStoreProRecordInfo--inputParams:{}",JacksonUtils.toJSONString(inputParams));
        String status = "success";
        String errorMsg = "";
        List<Map<String, Object>> result = new ArrayList<>();
        int allCount = 0;
        int retcount = 0;
        try {
            //先查列表数据
            Map<String, Object> qryParams = getSaveParams(inputParams);
            Map<String, Object> saveResult = storeProRecordService.save(qryParams);
            log.info("saveStoreProRecordInfo--saveresult:{}",JacksonUtils.toJSONString(saveResult));
            Map<String, Object> rtnMap = JacksonUtils.toMap(saveResult);
            if(!CollectionUtils.isEmpty(rtnMap) && rtnMap.get("failCount")!=null && rtnMap.get("failCount").toString().equals("1")){
                throw new Exception(rtnMap.get("messages").toString());
            }
            if(inputParams.containsKey("isUpdateMO") && inputParams.get("isUpdateMO").toString().equals("1")) {
                //更新生产订单已完工
                Map<String,Object> indata = ParamUtil.getindata(inputParams);
                String mocode = indata.get("mocode").toString();
                Map<String, Object> updatedataParams = new HashMap<>();
                Map<String, Object> updatedata = new HashMap<>();
                updatedata.put("mocode", mocode);
                updatedataParams.put("indata", updatedata);
                String updateMsg = productionOrderController.batchfinishWork(updatedataParams);
                log.info("batchfinishWork updateMsg:{}",updateMsg);
            }
            result.add(saveResult);
        }catch (Exception e){
            log.error("saveStoreProRecordInfo error:{}",e.getMessage(),e);
            errorMsg = e.getMessage();
            status = "error";
        }
        Map<String, Object> resultMap = ParamUtil.formatOutputParam(allCount, retcount,"storeProRecord", result, status, errorMsg);

        log.info("saveStoreProRecordInfo--resultMap:{}",JacksonUtils.toJSONString(resultMap));
        return JacksonUtils.toJSONString(resultMap);
    }
    /**
     * 构造保存参数
     * @param params
     * @return
     */
    private  Map<String, Object> getSaveParams(Map<String, Object> params) throws Exception {
        //2026-04-22 MES侧希望不构造产品入库单的保存接口，只传修改的物料信息，由ERP侧构造保存接口。修改如下：先根据来源单号查询产品入库单，再根据物料信息匹配修改的物料。
        // 先根据传入的参数查询产品入库单
        String rtnInfo =getStoreProRecordInfo(params);
        Map<String, Object> resultMap = JacksonUtils.toMap(rtnInfo);
        if (CollectionUtils.isEmpty(resultMap)) {
            throw new Exception("未查询到产品入库单信息");
        }
        Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");
        List<Map<String, Object>> datasMap = (List<Map<String, Object>>) dataMap.get("datas");
        if (CollectionUtils.isEmpty(datasMap)) {
            throw new Exception("未查询到产品入库单信息");
        }
        Map<String,Object> inputParams = ParamUtil.getindata(params);
        //根据外系统传参构造保存参数
        Map<String,Object> saveData = new HashMap<>();
        if(inputParams.containsKey("productinfo") && inputParams.get("productinfo") != null) {

            Map<String,Object> parentvo = (Map<String,Object>) datasMap.get(0).get("parentvo");
            //构造材料出库单表头对象
            Map<String,Object> saveParams = new HashMap<>();
            //根据映射文件转换参数
            Properties storeProRecordProps = ExchangeUtil.getProperty("storeProRecord.properties");
            saveParams = ExchangeUtil.transMap(parentvo, storeProRecordProps);
            //幂等key
            saveParams.put("resubmitCheckKey", UUID.randomUUID().toString().replace("-", ""));
            //操作标识
            saveParams.put("_status","Update");

            //构造产品入库单表体对象
            List<Map<String,Object>> childvo = (List<Map<String,Object>>) parentvo.get("childrenvo");
            List<Map<String,Object>> bodyParams = new ArrayList<>();
            //对现有产品入库单表体按物料编码匹配，匹配不到视为新增行
            List<Map<String, Object>> productinfo = (List<Map<String, Object>>) inputParams.get("productinfo");
            //传入的修改的信息只有一行，只有数量，不需要匹配，
            Map<String,Object> productinfo1 = productinfo.get(0);
            //for (Map<String, Object> productinfo1 : productinfo) {
                //String product_code =  productinfo1.get("product").toString();
                //List<Map<String, Object>> mapList = childvo.stream().filter(a->a.get("product_cCode").toString().equals(product_code)).collect(Collectors.toList());
                //if(CollectionUtils.isEmpty(mapList)){
                //    throw new Exception("产成品编码"+product_code+"不在产品入库单中");
                //}else {
                    //更新
                    Map<String,Object> childvo1 = childvo.get(0);
                    BigDecimal incomingQuantity = new BigDecimal(childvo1.get("incomingQuantity").toString());
                    BigDecimal inputQuantity = new BigDecimal(productinfo1.get("qty").toString());
                    incomingQuantity = incomingQuantity.add(inputQuantity);
                    //更新累计入库数量
                    childvo1.put("incomingQuantity",incomingQuantity);
                    childvo1.put("_status","Update");
                    BigDecimal inboundQuantity = new BigDecimal(childvo1.get("qty").toString());
                    if(incomingQuantity.compareTo(inboundQuantity)>=0){
                        params.put("isUpdateMO","1");
                    }
                    Map<String,Object> bodyParam = new HashMap<>();
                    //根据映射文件转换参数
                    Properties storeProRecordDetailProps = ExchangeUtil.getProperty("storeProRecords.properties");
                    bodyParam = ExchangeUtil.transMap(childvo1, storeProRecordDetailProps);
                    //20260627:正式环境增加存量敏感特征
                    if(childvo1.containsKey("storeProRecordsCharacteristics")){
                        bodyParam.put("storeProRecordsCharacteristics", childvo1.get("storeProRecordsCharacteristics"));
                    }
                    //20260630:正式环境增加回写标识
                    if(childvo1.containsKey("storeProRecordsDefineCharacter")){
                        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Map<String, Object> defineCharacter = (Map<String, Object>) childvo1.get("storeProRecordsDefineCharacter");
                        if(defineCharacter.containsKey("SFHC")){
                            String rtnMsg=defineCharacter.get("SFHC").toString();
                            defineCharacter.put("SFHC",  rtnMsg+"; "+date.format(new Date())+"MES回传数量"+inputQuantity);
                            defineCharacter.put("_status","Update");
                        }else{
                            defineCharacter.put("SFHC",  date.format(new Date())+"MES回传数量"+inputQuantity);
                            defineCharacter.put("_status","Update");
                        }
                        bodyParam.put("storeProRecordsDefineCharacter", defineCharacter);
                    }else{
                        Map<String, Object> defineCharacter = new HashMap<>();
                        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        defineCharacter.put("SFHC",  date.format(new Date())+"MES回传数量"+inputQuantity);
                        defineCharacter.put("_status","Insert");
                        bodyParam.put("storeProRecordsDefineCharacter", defineCharacter);
                    }
                    //操作标识
                    bodyParam.put("_status","Update");
                    //添加到表体列表
                    bodyParams.add(bodyParam);
                //}
            //}
            saveParams.put("storeProRecords", bodyParams);
            saveData.put("data",saveParams);
        }else{
            throw  new Exception("未传入保存参数productinfo");
        }
        return saveData;
    }
}