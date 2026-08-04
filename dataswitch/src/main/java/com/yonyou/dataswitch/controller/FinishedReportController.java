package com.yonyou.dataswitch.controller;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import com.yonyou.dataswitch.base.response.ApiDataPageResponse;
import com.yonyou.dataswitch.base.utils.JacksonUtils;
import com.yonyou.dataswitch.controller.util.ParamUtil;
import com.yonyou.dataswitch.service.FinishedReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;

/**
 * 对外接口：完工报告相关controller
 */
@Slf4j
@ResponseBody
@RestController
@RequestMapping("/dev/finishedReportController/v1")
public class FinishedReportController {

    @Resource
    FinishedReportService finishedReportService;

    @Resource
    ProductionOrderController productionOrderController;

    @Resource
    ProductController productController;

    /**
     * @param inputParams 查询参数
     * @return 返回success表示成功接收，抛出异常等其他代表接收失败
     */
    @PostMapping("/getFinishedReportInfo")
    public String getFinishedReportInfo(@RequestBody Map<String,Object> inputParams) throws Exception {
        //请求地址：http://localhost:8833/dev/finishedReportController/v1/getFinishedReportInfo
        //body:
        log.info("getFinishedReportInfo--inputParams:{}",JacksonUtils.toJSONString(inputParams));
        String status = "success";
        String errorMsg = "";
        List<Map<String, Object>> result = new ArrayList<>();
        int allCount = 0;
        int retcount = 0;
        try {
            //先查列表数据
            Map<String, Object> qryParams = getQryParams(inputParams);
            ApiDataPageResponse pageResponse = finishedReportService.list(qryParams);
            log.info("getFinishedReportInfo--listresult:{}",JacksonUtils.toJSONString(pageResponse));
            //再查详情数据：一条一条查询
            if(null!=pageResponse && !CollectionUtils.isEmpty(pageResponse.getRecordList())){
                allCount = pageResponse.getRecordCount();
                retcount = pageResponse.getRecordList().size();
                List<Map<String,Object>> allDetails = new ArrayList<>();
                for(Map<String, Object> tmpresult : pageResponse.getRecordList()){
                    Map<String,String> map = new HashMap<>();
                    map.put("id",tmpresult.get("id").toString());
                    Map<String, Object> detail =  finishedReportService.detail(map);
                    if(!CollectionUtils.isEmpty(detail)){
                        allDetails.add(detail);
                    }
                }
                result = allDetails;
            }
        }catch (Exception e){
            log.error("getFinishedReportInfo error:{}",e.getMessage(),e);
            errorMsg = e.getMessage();
            status = "error";
        }
        Map<String, Object> resultMap = ParamUtil.formatOutputParam(allCount, retcount,"finishedReport", result, status, errorMsg);

        log.info("getFinishedReportInfo--resultMap:{}",JacksonUtils.toJSONString(resultMap));
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
        //其他传入的字段待补充 TODO...
        return qryParams;
    }

    /**
     * @param inputParams 保存参数
     * @return 返回success表示成功接收，抛出异常等其他代表接收失败
     */
    @PostMapping("/saveFinishedReportInfo")
    public String saveFinishedReportInfo(@RequestBody Map<String,Object> inputParams) throws Exception {
        //请求地址：http://localhost:8833/dev/finishedReportController/v1/saveFinishedReportInfo
        //body:
        log.info("saveFinishedReportInfo--inputParams:{}",JacksonUtils.toJSONString(inputParams));
        String status = "success";
        String errorMsg = "";
        List<Map<String, Object>> result = new ArrayList<>();
        int allCount = 1;
        int retcount = 1;
        try {
            //先查列表数据
            Map<String, Object> qryParams = getSaveParams(inputParams);
            Map<String, Object> saveResult = finishedReportService.save(qryParams);
            log.info("saveFinishedReportInfo--saveresult:{}",JacksonUtils.toJSONString(saveResult));
            //保存成功后调用审核
            if(saveResult.containsKey("sucessCount") && "1".equals(saveResult.get("sucessCount").toString())) {
                List<Map<String, Object>> infos = (List<Map<String, Object>>) saveResult.get("infos");
                if (CollectionUtils.isEmpty(infos)) {
                    throw new Exception("保存成功但未返回单据信息");
                }
                Map<String, Object> vouchData = infos.get(0);
                String id = vouchData.get("id").toString();
                Map<String, Object> auditParam = new HashMap<>();
                Map<String, Object> auditData = new HashMap<>();
                auditData.put("id",id);
                auditParam.put("data",auditData);
                saveResult = finishedReportService.audit(auditParam);
                // 审核失败不抛异常
                //if(saveResult.containsKey("code") && !"200".equals(saveResult.get("code").toString())) {
                //    throw new Exception(saveResult.get("message").toString());
                //}
            }else{
                throw new Exception(saveResult.get("messages").toString());
            }
            result.add(saveResult);
        }catch (Exception e){
            log.error("saveFinishedReportInfo error:{}",e.getMessage(),e);
            errorMsg = e.getMessage();
            status = "error";
            retcount=0;
        }
        Map<String, Object> resultMap = ParamUtil.formatOutputParam(allCount, retcount,"finishedReport", result, status, errorMsg);

        log.info("saveFinishedReportInfo--resultMap:{}",JacksonUtils.toJSONString(resultMap));
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
        if(inputParams.containsKey("mocode") && inputParams.get("mocode") != null) {
            Map<String,Object> saveParams = new HashMap<>();
            saveParams.put("resubmitCheckKey", UUID.randomUUID().toString().replace("-", ""));
            //查询生产订单条件
            //Map<String,Object> queryinfo = (Map<String,Object>) inputParams.get("queryinfo");
            //查生产订单数据，获取生产订单行id
            Map<String, Object> qryParams = new HashMap<>();
            Map<String, Object> qryParam = new HashMap<>();
            qryParam.put("scddh", inputParams.get("mocode"));
            qryParams.put("indata", qryParam);
            String rtnInfo = productionOrderController.getProductionOrderInfo(qryParams);
            Map<String, Object> resultMap = JacksonUtils.toMap(rtnInfo);
            if(null!=resultMap && !CollectionUtils.isEmpty(resultMap)) {
                Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");
                List<Map<String, Object>> datasMap = (List<Map<String, Object>>) dataMap.get("datas");
                if(CollectionUtils.isEmpty(datasMap)){
                    throw  new Exception(String.format("未查询到生产订单%s数据，请检查生产订单号是否正确或者状态是否已开工",inputParams.get("mocode")));
                }
                Map<String, Object> orderMap = (Map<String, Object>)datasMap.get(0).get("parentvo");
                //计划完工数量（生产订单数量）
                BigDecimal jhwgsl = new BigDecimal(orderMap.get("jhwgsl").toString());
                //工厂编码
                saveParams.put("orgCode", inputParams.get("orgCode"));
                //交易类型 固定值取默认交易类型
                saveParams.put("transTypeId", "RF001");
                //单据日期
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                saveParams.put("vouchdate", simpleDateFormat.format(new Date()));
                //操作标识
                saveParams.put("_status", "Insert");
                //回写数量和批次信息
                List<Map<String, Object>> bodyParams = new ArrayList<>();
                if (inputParams.containsKey("productinfo") && inputParams.get("productinfo") != null) {
                    Map<String, Object> bodyParam = new HashMap<>();
                    List<Map<String, Object>> productinfos = (List<Map<String, Object>>) inputParams.get("productinfo");
                    Map<String, Object> productinfo = productinfos.get(0);
                    //产出类型 产成品
                    bodyParam.put("productionType", "0");
                    //来源单据产品行id
                    bodyParam.put("sourceautoid",orderMap.get("bodyid"));
                    //批次号
                    bodyParam.put("batchNo",productinfo.get("batchno"));
                    //先判断物料是否有效期管理，不是有效期管理的不能录生失效日期
                    Map<String, Object> invinfo = getProductInfoByCode(orderMap.get("inv_code").toString());
                    boolean expiryDateManage =false;
                    if(invinfo.containsKey("expiryDateManage") && invinfo.get("expiryDateManage") != null) {
                        expiryDateManage = (boolean) invinfo.get("expiryDateManage");
                    }
                    if(expiryDateManage){
                        //生效日期
                        String strProduceDate = "";
                        if(productinfo.containsKey("produceDate")) {
                            strProduceDate = productinfo.get("produceDate").toString();
                        }else{
                            strProduceDate = simpleDateFormat.format(new Date());
                        }
                        bodyParam.put("produceDate",strProduceDate);
                        //有效期至
                        String expirationDate = "";
                        if(productinfo.containsKey("expirationDate")) {
                            expirationDate = productinfo.get("expirationDate").toString();

                        }else{
                            //保质期单位 1=年，2=月，3=日
                            String expireDateUnit = invinfo.get("expireDateUnit").toString();
                            //保质期值
                            int expireDateNo = Integer.parseInt(invinfo.get("expireDateNo").toString());
                            //根据生产日期计算失效日期
                            expirationDate = getExpirationDate(strProduceDate, expireDateUnit, expireDateNo);
                        }
                        bodyParam.put("expirationDate", expirationDate);
                    }
//                    else{
//                        //2026-07-01 非效期管理的也传生效日期，失效日期=当天.试过了：传了ys也不接收
//                        //生效日期
//                        String strProduceDate = "";
//                        if(productinfo.containsKey("produceDate")) {
//                            strProduceDate = productinfo.get("produceDate").toString();
//                        }else{
//                            strProduceDate = simpleDateFormat.format(new Date());
//                        }
//                        bodyParam.put("produceDate",strProduceDate);
//                        bodyParam.put("expirationDate", simpleDateFormat.format(new Date()));
//                    }

                    //完工数量
                    BigDecimal wgsl = new BigDecimal(productinfo.get("qty").toString());
                    if(wgsl.compareTo(jhwgsl)>0){
                        //如果完工数量>计划完工数量，则以计划完工数量传给完工报告，否则完工报告保存会校验完工数量超量
                        wgsl = jhwgsl;
                    }
                    bodyParam.put("quantity",wgsl);
                    //产品检验
                    bodyParam.put("inspection",false);
                    //库存组织 现阶段库存组织和工厂是一个
                    bodyParam.put("orgCode",inputParams.get("orgCode"));
                    //20260627:正式环境增加存量敏感特征
                    if(orderMap.containsKey("freeCharacteristics")){
                        Map<String, Object> freeCharacteristics = (Map<String, Object>) orderMap.get("freeCharacteristics");
                        freeCharacteristics.remove("id");
                        freeCharacteristics.remove("pubts");
                        freeCharacteristics.put("_status",  "Insert");
                        bodyParam.put("freeCharacteristics", freeCharacteristics);
                    }
                    //操作标识
                    bodyParam.put("_status", "Insert");
                    bodyParams.add(bodyParam);
                } else {
                    throw new Exception("未传入保存参数productinfo");
                }
                saveParams.put("finishedReportDetail", bodyParams);
                saveData.put("data", saveParams);
            }else{
                throw  new Exception("未查询到生产订单数据");
            }
        }else{
            throw  new Exception("未传入生产订单号");
        }
        return saveData;
    }
    /**
     * 计算有效期至，并转换成yyyy-MM-dd格式日期
     * @param strProduceDate
     * @param expireDateUnit
     * @param expireDateNo
     * @return
     */
    private String getExpirationDate(String strProduceDate, String expireDateUnit, int expireDateNo) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String expirationDate;
        LocalDate Local_expireDate = calculateExpiryDate(LocalDate.parse(strProduceDate), expireDateUnit, expireDateNo);
        //计算有效期至=失效日期减1天
        Instant instant = Local_expireDate.minusDays(1L).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        Date expireDate = Date.from(instant);
        expirationDate = simpleDateFormat.format(expireDate);
        return expirationDate;
    }

    /**
     * 根据物料编码查询物料详情
     * @param productCode
     * @return
     * @throws Exception
     */
    private Map<String, Object> getProductInfoByCode(String productCode) throws Exception {
        Map<String, Object> qryProductParams = new HashMap<>();
        Map<String, Object> qryproductdata = new HashMap<>();

        qryproductdata.put("invcode", productCode);
        qryProductParams.put("indata", qryproductdata);
        String controllerProductInfo = productController.getProductInfo(qryProductParams);
        Map<String, Object> productMap = JacksonUtils.toMap(controllerProductInfo);
        if (CollectionUtils.isEmpty(productMap)) {
            throw new Exception("未查询到物料信息，物料编码=" + productCode);
        }
        Map<String, Object> productdataMap = (Map<String, Object>) productMap.get("data");
        List<Map<String, Object>> productdatasMap = (List<Map<String, Object>>) productdataMap.get("datas");
        if (CollectionUtils.isEmpty(productdatasMap)) {
            throw new Exception("未查询到物料信息，物料编码=" + productCode);
        }
        Map<String, Object> productdata = productdatasMap.get(0);
        Map<String,Object> productparventvo= (Map<String,Object>)productdata.get("parentvo");
        return productparventvo;
    }

    /**
     * 根据生产日期计算失效日期
     * @param productionDate
     * @param expiryUnit
     * @param expiryValue
     * @return
     */
    private LocalDate calculateExpiryDate(LocalDate productionDate, String expiryUnit, int expiryValue) {
        switch (expiryUnit.toUpperCase()) {
            //物料业务信息上枚举值={1-年,2-月,6-天}
            case "6":
            case "3":
            case "DAYS":
                return productionDate.plusDays(expiryValue);
            case "2":
            case "MONTHS":
                return productionDate.plusMonths(expiryValue);
            case "1":
            case "YEARS":
                return productionDate.plusYears(expiryValue);
            default:
                throw new IllegalArgumentException("Unsupported expiry unit: " + expiryUnit);
        }
    }
}