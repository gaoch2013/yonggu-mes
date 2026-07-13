package com.yonyou.dataswitch.controller;

import com.yonyou.dataswitch.base.response.ApiDataPageResponse;
import com.yonyou.dataswitch.base.utils.JacksonUtils;
import com.yonyou.dataswitch.controller.util.ParamUtil;
import com.yonyou.dataswitch.controller.util.PubUtil;
import com.yonyou.dataswitch.service.OthoutRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 对外接口：其他出库单相关controller
 */
@Slf4j
@ResponseBody
@RestController
@RequestMapping("/dev/othOutRecordController/v1")
public class OthOutRecordController {

    @Resource
    OthoutRecordService othoutRecordService;
    @Resource
    PubUtil pubUtil;
    /**
     * @param inputParams 查询参数
     * @return 返回success表示成功接收，抛出异常等其他代表接收失败
     */
    @PostMapping("/getOthOutRecordInfo")
    public String getOthOutRecordInfo(@RequestBody Map<String,Object> inputParams) throws Exception {
        //请求地址：http://localhost:8833/dev/othOutRecordController/v1/getOthOutRecordInfo
        //body:
        log.info("getOthOutRecordInfo--inputParams:{}",JacksonUtils.toJSONString(inputParams));
        String status = "success";
        String errorMsg = "";
        List<Map<String, Object>> result = new ArrayList<>();
        int allCount = 0;
        int retcount = 0;
        try {
            //先查列表数据
            Map<String, Object> qryParams = getQryParams(inputParams);
            ApiDataPageResponse pageResponse = othoutRecordService.list(qryParams);
            log.info("getOthOutRecordInfo--listresult:{}",JacksonUtils.toJSONString(pageResponse));
            //再查详情数据：一条一条查询
            if(null!=pageResponse && !CollectionUtils.isEmpty(pageResponse.getRecordList())){
                allCount = pageResponse.getRecordCount();
                retcount = pageResponse.getRecordList().size();
                List<Map<String,Object>> allDetails = new ArrayList<>();
                for(Map<String, Object> tmpresult : pageResponse.getRecordList()){
                    Map<String,String> map = new HashMap<>();
                    map.put("id",tmpresult.get("id").toString());
                    Map<String, Object> detail =  othoutRecordService.detail(map);
                    if(!CollectionUtils.isEmpty(detail)){
                        detail = ParamUtil.formatSingleData("othOutRecord", detail);
                        allDetails.add(detail);
                    }
                }
                result = allDetails;
            }
        }catch (Exception e){
            log.error("getOthOutRecordInfo error:{}",e.getMessage(),e);
            errorMsg = e.getMessage();
            status = "error";
        }
        Map<String, Object> resultMap = ParamUtil.formatOutputParam(allCount, retcount,"othOutRecord", result, status, errorMsg);

        log.info("getOthOutRecordInfo--resultMap:{}",JacksonUtils.toJSONString(resultMap));
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
        qryParams.put("code", inputParams.get("cbillcode"));
        //
        return qryParams;
    }

    /**
     * @param inputParams 保存参数
     * @return 返回success表示成功接收，抛出异常等其他代表接收失败
     */
    @PostMapping("/saveOthOutRecordInfo")
    public String saveOthOutRecordInfo(@RequestBody Map<String,Object> inputParams) throws Exception {
        //请求地址：http://localhost:8833/dev/othOutRecordController/v1/saveOthOutRecordInfo
        //body:
        log.info("saveOthOutRecordInfo--inputParams:{}",JacksonUtils.toJSONString(inputParams));
        String status = "success";
        String errorMsg = "";
        List<Map<String, Object>> result = new ArrayList<>();
        int allCount = 0;
        int retcount = 0;
        try {
            //构造保存数据
            Map<String, Object> saveData = getSaveParams(inputParams);
            Map<String, Object> saveResult = othoutRecordService.save(saveData);
            log.info("saveOthOutRecordInfo--saveresult:{}",JacksonUtils.toJSONString(saveResult));
            result.add(saveResult);
        }catch (Exception e){
            log.error("saveOthOutRecordInfo error:{}",e.getMessage(),e);
            errorMsg = e.getMessage();
            status = "error";
        }

        Map<String, Object> resultMap = ParamUtil.formatOutputParam(allCount, retcount,"othOutRecord", result, status, errorMsg);

        log.info("saveOthOutRecordInfo--resultMap:{}",JacksonUtils.toJSONString(resultMap));
        return JacksonUtils.toJSONString(resultMap);
    }
    /**
     * 构造保存参数
     * @param params
     * @return
     */
    private Map<String, Object> getSaveParams(Map<String, Object> params) throws Exception {
        Map<String,Object> inputParams = ParamUtil.getindata(params);
        //根据外系统传参构造保存参数
        Map<String,Object> saveData = new HashMap<>();
        //解析易加传入的保存参数
            Map<String,Object> parentvo = inputParams;
            //表头
            Map<String,Object> saveParams = new HashMap<>();
            saveParams.put("resubmitCheckKey", UUID.randomUUID().toString().replace("-", ""));
            //新增保存，单据id不传
            //库存组织
            saveParams.put("org", parentvo.get("orgCode"));
            //仓库
            saveParams.put("warehouse", parentvo.get("cwarehouseid"));
            //单据日期
            SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
            saveParams.put("vouchdate", date.format(new Date()));
            //saveParams.put("vouchdate", parentvo.get("vouchdate"));
            //业务类型
            saveParams.put("bustype", parentvo.get("cdispatcherid"));
            //会计主体
            saveParams.put("accountOrg", parentvo.get("accountOrg"));
            //部门
            saveParams.put("department", parentvo.get("cdptid"));
            //库管员
            saveParams.put("stockMgr", parentvo.get("cwhsmanagerid"));
            //业务员
            saveParams.put("operator", parentvo.get("coperatorid"));
            //操作标识
            saveParams.put("id", parentvo.get("id"));
            saveParams.put("pubts", parentvo.get("pubts"));
            saveParams.put("_status", "Insert");
            //表体
            List<Map<String,Object>> childvo = (List<Map<String,Object>>) inputParams.get("othOutRecords");
            List<Map<String,Object>> bodyParams = new ArrayList<>();
            for(Map<String,Object> childvo1 : childvo){
                Map<String,Object> bodyParam = new HashMap<>();
                bodyParam.put("product",childvo1.get("cinventoryid"));
                bodyParam.put("productsku",childvo1.get("cinventoryid"));
                bodyParam.put("batchno",childvo1.get("vbatchcode"));
                //根据物料编码查询物料详情，目的是取物料id查询对应批次的效期
                String productCode= childvo1.get("cinventoryid").toString();
                Map<String, Object> productdata = pubUtil.getProductInfoByCode(productCode);
                //根据物料id+批次查询效期
                Object productId = productdata.get("id");
                Map<String, Object> batchnodata = pubUtil.getBatchNoInfoByProduct(productId, childvo1.get("vbatchcode").toString(), productCode);
                bodyParam.put("producedate", batchnodata.get("producedate"));
                bodyParam.put("invaliddate", batchnodata.get("invaliddate"));
                //货位 永固客户没有启用货位
                //bodyParam.put("goodsposition","000001");
                bodyParam.put("qty",childvo1.get("noutnum"));
                //辅计量数量
                BigDecimal qty = new BigDecimal(childvo1.get("noutnum").toString());
                BigDecimal invExchRate = new BigDecimal(productdata.get("invExchRate").toString());
                BigDecimal subQty = qty.divide(invExchRate,8,BigDecimal.ROUND_HALF_UP);
                bodyParam.put("subQty", subQty.toString());
                //主计量为非必输项
                bodyParam.put("unit",productdata.get("meascode"));
                bodyParam.put("stockUnitId",productdata.get("stockUnitCode"));
                bodyParam.put("invExchRate",productdata.get("invExchRate"));
                bodyParam.put("unitExchangeType",productdata.get("unitExchangeType"));
                bodyParam.put("_status",  "Insert");
                //20260627:正式环境增加存量敏感特征
                //根据仓库+物料+批次查询存量数据里的敏感特征数据
                Map<String,Object> currentStockCharacteristic = pubUtil.getCurrentStockCharacteristic(parentvo.get("cwarehouseid").toString(),productCode,childvo1.get("vbatchcode").toString());
                if(null!=currentStockCharacteristic){
                    currentStockCharacteristic.remove("id");
                    currentStockCharacteristic.remove("pubts");
                    currentStockCharacteristic.put("_status",  "Insert");
                    bodyParam.put("othOutRecordsCharacteristics",currentStockCharacteristic);
                }
                //20260630:正式环境增加回写标识
                Map<String, Object> defineCharacter = new HashMap<>();
                SimpleDateFormat date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                defineCharacter.put("SFHC",  date1.format(new Date())+"MES回传数量"+qty);
                defineCharacter.put("_status","Insert");
                bodyParam.put("othOutRecordsDefineCharacter", defineCharacter);

                bodyParams.add(bodyParam);
            }
            saveParams.put("othOutRecords", bodyParams);
            saveData.put("data",saveParams);
        return saveData;
    }
}