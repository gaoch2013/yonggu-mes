package com.yonyou.dataswitch.controller;

import com.yonyou.dataswitch.base.response.ApiDataPageResponse;
import com.yonyou.dataswitch.base.utils.JacksonUtils;
import com.yonyou.dataswitch.controller.util.DeepCopyUtil;
import com.yonyou.dataswitch.controller.util.ExchangeUtil;
import com.yonyou.dataswitch.controller.util.ParamUtil;
import com.yonyou.dataswitch.controller.util.PubUtil;
import com.yonyou.dataswitch.service.MaterialOutService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 对外接口：材料出库单相关controller
 */
@Slf4j
@ResponseBody
@RestController
@RequestMapping("/dev/materialOutController/v1")
public class MaterialOutController {

    @Resource
    MaterialOutService materialOutService;
    @Resource
    PubUtil pubUtil;
    /**
     * @param inputParams 查询参数
     * @return 返回success表示成功接收，抛出异常等其他代表接收失败
     */
    @PostMapping("/getMaterialOutInfo")
    public String getMaterialOutInfo(@RequestBody Map<String,Object> inputParams) throws Exception {
        //请求地址：http://localhost:8833/dev/materialOutController/v1/getMaterialOutInfo
        //body:
        log.info("getMaterialOutInfo--inputParams:{}",JacksonUtils.toJSONString(inputParams));
        String status = "success";
        String errorMsg = "";
        List<Map<String, Object>> result = new ArrayList<>();
        int allCount = 0;
        int retcount = 0;
        try {
            //先查列表数据
            Map<String, Object> qryParams = getQryParams(inputParams);
            ApiDataPageResponse pageResponse = materialOutService.list(qryParams);
            log.info("getMaterialOutInfo--listresult:{}",JacksonUtils.toJSONString(pageResponse));
            //再查详情数据：一条一条查询
            if(null!=pageResponse && !CollectionUtils.isEmpty(pageResponse.getRecordList())){
                allCount = pageResponse.getRecordCount();
                retcount = pageResponse.getRecordList().size();
                List<Map<String,Object>> allDetails = new ArrayList<>();
                for(Map<String, Object> tmpresult : pageResponse.getRecordList()){
                    Map<String,String> map = new HashMap<>();
                    map.put("id",tmpresult.get("id").toString());
                    Map<String, Object> detail =  materialOutService.detail(map);
                    if(!CollectionUtils.isEmpty(detail)){
                        detail = ParamUtil.formatSingleData("materialOut", detail);
                        allDetails.add(detail);
                    }
                }
                result = allDetails;
            }
        }catch (Exception e){
            log.error("getMaterialOutInfo error:{}",e.getMessage(),e);
            errorMsg = e.getMessage();
            status = "error";
        }
        Map<String, Object> resultMap = ParamUtil.formatOutputParam(allCount, retcount,"materialOut", result, status, errorMsg);

        log.info("getMaterialOutInfo--resultMap:{}",JacksonUtils.toJSONString(resultMap));
        return JacksonUtils.toJSONString(resultMap);
    }

    /**
     * 构造查询参数
     * @param params
     * @return
     */
    private static Map<String, Object> getQryParams(Map<String, Object> params) throws Exception {
        Map<String,Object> inputParams = ParamUtil.getindata(params);
        Map<String,Object> qryParams = new HashMap<>();
        qryParams.put("pageIndex", inputParams.getOrDefault("page_now", 1));
        qryParams.put("pageSize", inputParams.getOrDefault("page_size", 100));
        qryParams.put("isSum", true);//固定条件
        //按来源单号查询 如果按源头单据号查，就把upcode改成topupcode
        List<Map<String, Object>> simpleVOs = new ArrayList<>();
        if(inputParams.containsKey("sourcebillcode") && inputParams.get("sourcebillcode") != null) {
            Map<String, Object> simpleVO = new HashMap<>();
            simpleVO.put("field", "materOuts.upcode");
            simpleVO.put("op", "eq");
            simpleVO.put("value1", inputParams.get("sourcebillcode"));
            simpleVOs.add(simpleVO);
        }
        qryParams.put("simpleVOs", simpleVOs);

        return qryParams;
    }

    /**
     * @param inputParams 保存参数
     * @return 返回success表示成功接收，抛出异常等其他代表接收失败
     */
    @PostMapping("/saveMaterialOutInfo")
    public String saveMaterialOutInfo(@RequestBody Map<String,Object> inputParams) throws Exception {
        //请求地址：http://localhost:8833/dev/materialOutController/v1/saveMaterialOutInfo
        //body:
        log.info("saveMaterialOutInfo--inputParams:{}",JacksonUtils.toJSONString(inputParams));
        String status = "success";
        String errorMsg = "";
        List<Map<String, Object>> result = new ArrayList<>();
        int allCount = 0;
        int retcount = 0;
        try {
            //先查列表数据
            Map<String, Object> qryParams = getSaveParams(inputParams);
            Map<String, Object> saveResult = materialOutService.save(qryParams);
            log.info("saveMaterialOutInfo--saveresult:{}",JacksonUtils.toJSONString(saveResult));
            Map<String, Object> rtnMap = JacksonUtils.toMap(saveResult);
            if(!CollectionUtils.isEmpty(rtnMap) && rtnMap.get("failCount")!=null && rtnMap.get("failCount").toString().equals("1")){
                throw new Exception(rtnMap.get("messages").toString());
            }
            result.add(saveResult);
        }catch (Exception e){
            log.error("saveMaterialOutInfo error:{}",e.getMessage(),e);
            errorMsg = e.getMessage();
            status = "error";
        }
        Map<String, Object> resultMap = ParamUtil.formatOutputParam(allCount, retcount,"materialOut", result, status, errorMsg);

        log.info("saveMaterialOutInfo--resultMap:{}",JacksonUtils.toJSONString(resultMap));
        return JacksonUtils.toJSONString(resultMap);
    }
    /**
     * 构造保存参数
     * @param params
     * @return
     */
    private  Map<String, Object> getSaveParams(Map<String, Object> params) throws Exception {
        //2026-04-22 MES侧希望不构造材料出库的保存接口，只传修改的物料信息，由ERP侧构造保存接口。修改如下：先根据来源单号查询材料出，再根据物料+批次信息匹配修改的物料。
        // 先根据传入的参数查询材料出库单
        String rtnInfo =getMaterialOutInfo(params);
        Map<String, Object> resultMap = JacksonUtils.toMap(rtnInfo);
        if (CollectionUtils.isEmpty(resultMap)) {
            throw new Exception("未查询到材料出库单信息");
        }
        Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");
        List<Map<String, Object>> datasMap = (List<Map<String, Object>>) dataMap.get("datas");
        if (CollectionUtils.isEmpty(datasMap)) {
            throw new Exception("未查询到材料出库单信息");
        }
        Map<String,Object> inputParams = ParamUtil.getindata(params);
        //根据外系统传参构造保存参数
        Map<String,Object> saveData = new HashMap<>();
        if(inputParams.containsKey("productinfo") && inputParams.get("productinfo") != null) {

            Map<String,Object> parentvo = (Map<String,Object>) datasMap.get(0).get("parentvo");
            //构造材料出库单表头对象
            Map<String,Object> updateData = new HashMap<>();
            //根据映射文件转换参数
            Properties materialOutProps = ExchangeUtil.getProperty("materialOut.properties");
            updateData = ExchangeUtil.transMap(parentvo, materialOutProps);
            //幂等key
            updateData.put("resubmitCheckKey", UUID.randomUUID().toString().replace("-", ""));
            //操作标识
            updateData.put("_status","Update");

            //材料出库单表体数据
            List<Map<String,Object>> childrenvo = (List<Map<String,Object>>) parentvo.get("childrenvo");
            //mes传过来的出库单表体更新数据
            List<Map<String, Object>> productinfo = (List<Map<String, Object>>) inputParams.get("productinfo");
            List<Map<String,Object>> bodyParams = new ArrayList<>();
            //对现有材料出库单表体按物料编码+批次匹配，匹配不到视为新增行
            for(Map<String,Object> childvo1 : childrenvo){
                String tmpkey = childvo1.get("product_cCode").toString()+(childvo1.get("batchno")==null?"":childvo1.get("batchno").toString());
                childvo1.put("groupkey", tmpkey);
                childvo1.put("_status","UnChanged");//更新标识
            }
            Map<String, List<Map<String, Object>>> childvoGroupMap = childrenvo.stream().collect(Collectors.groupingBy(a->a.get("groupkey").toString()));

            //传入的修改的物料信息是物料编码+批次，启用了有效期管理,没货位信息
            /**
             * 匹配逻辑
             * 1、按物料+批次匹配材料出库单表体，匹配到时更新匹配记录的数量；
             * 2、匹配不到时，包含改了批次和新增批次的情况
             * 示例1：
             * 材料出库单 WL1 PC1 10
             * 回写的数据 WL1 PC1 5
             * 回写的数据 WL1 PC2 3
             * 回写的数据 WL1 PC3 2
             * 示例2：
             * 材料出库单 WL1  ?  10
             * 回写的数据 WL1 PC1 5
             * 回写的数据 WL1 PC2 3
             * 回写的数据 WL1 PC3 2
             */
            //匹配出库单表体数据
            for (int i= productinfo.size()-1;i>=0;i--) {
                //物料+批次匹配
                Map<String, Object> productinfo0 = productinfo.get(i);
                String tmpkey = productinfo0.get("product").toString() + (productinfo0.get("batchno") == null ? "" : productinfo0.get("batchno").toString());
                List<Map<String, Object>> mapList0 = childvoGroupMap.get(tmpkey);
                if (!CollectionUtils.isEmpty(mapList0)) {
                    //匹配到的材料出库单表体只会有一条记录，所以取第0条即可。更新数量 确认应发数量不更新。
                    Map<String, Object> childvo = mapList0.get(0);
                    childvo.put("qty", productinfo0.get("qty"));
                    //根据物料编码查询物料详情
                    String productCode= productinfo0.get("product").toString();
                    Map<String, Object> productdata = pubUtil.getProductInfoByCode(productCode);
                    //辅计量数量
                    BigDecimal qty = new BigDecimal(productinfo0.get("qty").toString());
                    BigDecimal invExchRate = new BigDecimal(productdata.get("invExchRate").toString());
                    BigDecimal subQty = qty.divide(invExchRate,8,BigDecimal.ROUND_HALF_UP);
                    childvo.put("subQty", subQty.toString());
                    childvo.put("_status","Update");
                    Map<String, Object> bodyParam = new HashMap<>();
                    //根据映射文件转换参数
                    Properties materialOutDetailProps = ExchangeUtil.getProperty("materialOutDetail.properties");
                    bodyParam = ExchangeUtil.transMap(childvo, materialOutDetailProps);
                    //20260627:正式环境增加存量敏感特征
                    if(childvo.containsKey("materialOutsCharacteristics")){
                        bodyParam.put("materialOutsCharacteristics", childvo.get("materialOutsCharacteristics"));
                    }
                    //20260630:正式环境增加回写标识
                    if(childvo.containsKey("materialOutsDefineCharacter")){
                        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Map<String, Object> defineCharacter = (Map<String, Object>) childvo.get("materialOutsDefineCharacter");
                        if(defineCharacter.containsKey("SFHC")){
                            String rtnMsg=defineCharacter.get("SFHC").toString();
                            defineCharacter.put("SFHC",  rtnMsg+"; "+date.format(new Date())+"MES回传数量"+qty);
                            defineCharacter.put("_status","Update");
                        }else{
                            defineCharacter.put("SFHC",  date.format(new Date())+"MES回传数量"+qty);
                            defineCharacter.put("_status","Update");
                        }
                        bodyParam.put("materialOutsDefineCharacter", defineCharacter);
                    }else{
                        Map<String, Object> defineCharacter = new HashMap<>();
                        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        defineCharacter.put("SFHC",  date.format(new Date())+"MES回传数量"+qty);
                        defineCharacter.put("_status","Insert");
                        bodyParam.put("materialOutsDefineCharacter", defineCharacter);
                    }
                    //操作标识
                    bodyParam.put("_status", "Update");
                    //添加到表体列表
                    bodyParams.add(bodyParam);
                    //匹配到后排除掉这条传入的数据，避免后续重复判断
                    productinfo.remove(i);
                }else {
                 //按物料匹配
                    Map<String, Object> productinfo1 = productinfo.get(i);
                    List<Map<String, Object>> mapList1 = childrenvo.stream().filter(a -> a.get("product_cCode").toString().equals(productinfo1.get("product").toString()) && !a.get("_status").equals("Insert")).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(mapList1)) {
                        Map<String, Object> childvo = mapList1.get(0);
                        if (childvo.get("_status").equals("Update")) {
                            //如果此时匹配到的表体是更新态的，说明是一个物料+批次不满足，拆分了。此时把表体复制，并按传过来的数据更新批次+数量
                            Map<String, Object> childvo2 = DeepCopyUtil.deepCopy(childvo);
                            String batchno = productinfo1.get("batchno").toString();
                            childvo2.put("batchno", batchno);
                            //根据物料编码查询物料详情，目的是取物料id查询对应批次的效期
                            String productCode= productinfo1.get("product").toString();
                            Map<String, Object> productdata = pubUtil.getProductInfoByCode(productCode);
                            //根据物料id+批次查询效期
                            Object productId = productdata.get("id");
                            Map<String, Object> batchnodata = pubUtil.getBatchNoInfoByProduct(productId, batchno, productCode);
                            childvo2.put("producedate", batchnodata.get("producedate"));
                            childvo2.put("invaliddate", batchnodata.get("invaliddate"));
                            childvo2.put("qty", productinfo1.get("qty"));
                            //辅计量数量
                            BigDecimal qty = new BigDecimal(productinfo1.get("qty").toString());
                            BigDecimal invExchRate = new BigDecimal(productdata.get("invExchRate").toString());
                            BigDecimal subQty = qty.divide(invExchRate,8,BigDecimal.ROUND_HALF_UP);
                            childvo2.put("subQty", subQty.toString());
                            //货位 永固客户没有启用货位
                            //childvo2.put("goodsposition", "000001");
                            childvo2.remove("id");
                            childvo2.remove("pubts");
                            childvo2.put("_status", "Insert");
                            Map<String, Object> bodyParam = new HashMap<>();
                            //根据映射文件转换参数
                            Properties materialOutDetailProps = ExchangeUtil.getProperty("materialOutDetail.properties");
                            bodyParam = ExchangeUtil.transMap(childvo2, materialOutDetailProps);
                            //20260627:正式环境增加存量敏感特征
                            //根据仓库+物料+批次查询存量数据里的敏感特征数据
                            Map<String,Object> currentStockCharacteristic = pubUtil.getCurrentStockCharacteristic(parentvo.get("warehouse").toString(),productCode,batchno);
                            if(null!=currentStockCharacteristic){
                                currentStockCharacteristic.remove("id");
                                currentStockCharacteristic.remove("pubts");
                                currentStockCharacteristic.put("_status",  "Insert");
                                bodyParam.put("materialOutsCharacteristics",currentStockCharacteristic);
                            }
                            //20260630:正式环境增加回写标识
                            Map<String, Object> defineCharacter = new HashMap<>();
                            SimpleDateFormat date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            defineCharacter.put("SFHC",  date1.format(new Date())+"MES回传数量"+qty);
                            defineCharacter.put("_status","Insert");
                            bodyParam.put("materialOutsDefineCharacter", defineCharacter);
                            //操作标识
                            bodyParam.put("_status", "Insert");
                            //添加到表体列表
                            bodyParams.add(bodyParam);
                            productinfo.remove(i);
                        }else if (childvo.get("_status").equals("UnChanged")) {
                            //如果此时匹配到的表体是未修改态的，说明是改了批次号或者新增了批次号。此时把表体按传过来的数据更新批次+数量
                            String batchno = productinfo1.get("batchno").toString();
                            childvo.put("batchno", batchno);
                            //根据物料查询
                            String productCode= productinfo1.get("product").toString();
                            Map<String, Object> productdata = pubUtil.getProductInfoByCode(productCode);
                            //根据物料+批次查询效期
                            Object productId = productdata.get("id");
                            Map<String, Object> batchnodata = pubUtil.getBatchNoInfoByProduct(productId, batchno, productCode);
                            childvo.put("producedate", batchnodata.get("producedate"));
                            childvo.put("invaliddate", batchnodata.get("invaliddate"));
                            childvo.put("qty", productinfo1.get("qty"));
                            //辅计量数量
                            BigDecimal qty = new BigDecimal(productinfo1.get("qty").toString());
                            BigDecimal invExchRate = new BigDecimal(productdata.get("invExchRate").toString());
                            BigDecimal subQty = qty.divide(invExchRate,8,BigDecimal.ROUND_HALF_UP);
                            childvo.put("subQty", subQty.toString());
                            //货位 永固客户没有启用货位
                            //childvo.put("goodsposition", "000001");
                            childvo.put("_status", "Update");
                            Map<String, Object> bodyParam = new HashMap<>();
                            //根据映射文件转换参数
                            Properties materialOutDetailProps = ExchangeUtil.getProperty("materialOutDetail.properties");
                            bodyParam = ExchangeUtil.transMap(childvo, materialOutDetailProps);
                            //20260627:正式环境增加存量敏感特征
                            //根据仓库+物料+批次查询存量数据里的敏感特征数据
                            Map<String,Object> currentStockCharacteristic = pubUtil.getCurrentStockCharacteristic(parentvo.get("warehouse").toString(),productCode,batchno);
                            if(null!=currentStockCharacteristic){
                                if(childvo.containsKey("materialOutsCharacteristics")){
                                    Map<String, Object> materialOutsCharacteristics = (Map<String, Object>)childvo.get("materialOutsCharacteristics");
                                    materialOutsCharacteristics.put("BOMbanben",currentStockCharacteristic.get("BOMbanben"));
                                    materialOutsCharacteristics.put("BOMbanben_name",currentStockCharacteristic.get("BOMbanben_name"));
                                    bodyParam.put("materialOutsCharacteristics", materialOutsCharacteristics);
                                }else{
                                    currentStockCharacteristic.remove("id");
                                    currentStockCharacteristic.remove("pubts");
                                    currentStockCharacteristic.put("_status",  "Insert");
                                    bodyParam.put("materialOutsCharacteristics",currentStockCharacteristic);
                                }
                            }
                            //20260630:正式环境增加回写标识
                            Map<String, Object> defineCharacter = new HashMap<>();
                            SimpleDateFormat date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            defineCharacter.put("SFHC",  date1.format(new Date())+"MES回传数量"+qty);
                            defineCharacter.put("_status","Insert");
                            bodyParam.put("materialOutsDefineCharacter", defineCharacter);
                            //操作标识
                            bodyParam.put("_status", "Update");
                            //添加到表体列表
                            bodyParams.add(bodyParam);
                            productinfo.remove(i);
                        }
                    }
                }
            }
            updateData.put("materOuts", bodyParams);
            saveData.put("data",updateData);
        }else{
            throw  new Exception("未传入保存参数productinfo");
        }
        return saveData;
    }


}