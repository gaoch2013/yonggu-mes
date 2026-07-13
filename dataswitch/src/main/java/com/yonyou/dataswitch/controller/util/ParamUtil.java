package com.yonyou.dataswitch.controller.util;

import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParamUtil {
    public static Map<String,Object> getindata(Map<String,Object> param) throws Exception {
       if(null == param || param.isEmpty() || !param.containsKey("indata")){
           throw new Exception("参数错误");
       }
        return (Map<String,Object>)param.get("indata");
    }

    /**
     * 格式化详情数据 格式：
     *     "parentvo": {
     *         "field1": "value1",
     *         "field2": "value2",
     *         "childrenvo": [
     *             {
     *                 "field3": "value3",
     *                 "field4": "value4"
     *             }
     *         ]
     *     }
     * }
     * @param billnum
     * @param objData
     * @return
     * @throws Exception
     */
    public static Map<String,Object> formatSingleData(String billnum, Map<String, Object> objData) throws Exception {
        //现存量记录
        if("stockInfo".equals(billnum)){
            Map<String,Object> tmpMap = new HashMap<>();
            tmpMap.put("nnum", objData.get("currentqty"));
            tmpMap.put("product_code", objData.get("product_code"));
            tmpMap.put("warehouse_code", objData.get("warehouse_code"));
            tmpMap.put("batchno", objData.get("batchno"));
            tmpMap.put("id", objData.get("id"));
            //20260627:正式环境增加存量敏感特征
            if(objData.containsKey("currentStockCharacteristic")) {
                tmpMap.put("currentStockCharacteristic", objData.get("currentStockCharacteristic"));
            }
            return tmpMap;
        }
        String detailKey = ExchangeUtil.getDetailKey(billnum);
        //子表名用childrenvo表示
        Map<String,Object> dataMap = new HashMap<>();
        Map<String,Object> resultMap = new HashMap<>(objData);
        resultMap.remove(detailKey);
        if(null!=objData && null!=objData.get(detailKey)){
            //物料表体不是list结构
            if(billnum.equals("product")){
                Map<String,Object> detailData = (Map<String,Object>)objData.get(detailKey);
                resultMap.put("stockUnitCode", detailData.get("stockUnitCode"));
                resultMap.put("purchaseUnitCode", detailData.get("purchaseUnitCode"));
                resultMap.put("batchUnitCode", detailData.get("batchUnitCode"));
                resultMap.put("batchPriceUnitCode", detailData.get("batchPriceUnitCode"));
                resultMap.put("requireUnitCode", detailData.get("requireUnitCode"));
                resultMap.put("produceUnitCode", detailData.get("produceUnitCode"));
                resultMap.put("deliveryWarehouseCode", detailData.get("deliveryWarehouseCode"));
                resultMap.put("mnemonicCode", detailData.get("mnemonicCode"));
                resultMap.put("expiryDateManage",detailData.get("expiryDateManage"));
                resultMap.put("expireDateNo", detailData.get("expireDateNo"));
                resultMap.put("expireDateUnit", detailData.get("expireDateUnit"));
                if(resultMap.containsKey("name") && null!=resultMap.get("name")) {
                    Map<String, Object> name = (Map<String, Object>) resultMap.get("name");
                    resultMap.put("name_name", name.get("simplifiedName"));
                }
                if(resultMap.containsKey("model") && null!=resultMap.get("model")) {
                    Map<String, Object> name = (Map<String, Object>) resultMap.get("model");
                    resultMap.put("model_name", name.get("simplifiedName"));
                }
                if(resultMap.containsKey("modelDescription") && null!=resultMap.get("modelDescription")) {
                    Map<String, Object> name = (Map<String, Object>) resultMap.get("modelDescription");
                    resultMap.put("modelDescription_name", name.get("simplifiedName"));
                }
                //换算率；
                BigDecimal invExchRate = new BigDecimal("1");
                String unitExchangeType ="0";
                if(objData.containsKey("productAssistUnitExchanges")){
                    List<Map<String,Object>> productAssistUnitExchanges = (List<Map<String,Object>>)objData.get("productAssistUnitExchanges");
                    if(CollectionUtils.isNotEmpty(productAssistUnitExchanges)){
                        Map<String,Object> productAssistUnitExchange = productAssistUnitExchanges.get(0);
                        //主辅计量数的值是Double类型，需要转换成BigDecimal来做运算，避免精度丢失
                        BigDecimal assistUnitCount = new BigDecimal((Double)productAssistUnitExchange.get("assistUnitCount"));
                        BigDecimal mainUnitCount = new BigDecimal((Double)productAssistUnitExchange.get("mainUnitCount"));
                        // 1支=20g,主计量=g，辅计量=支，则换算率=20/1.
                        invExchRate = mainUnitCount.divide(assistUnitCount,8, BigDecimal.ROUND_HALF_UP);
                        //换算方式，0：固定、1：浮动
                        unitExchangeType = productAssistUnitExchange.get("unitExchangeType").toString();
                    }
                }
                resultMap.put("unitExchangeType", unitExchangeType);
                resultMap.put("invExchRate", invExchRate.toString());

            }else {
                List<Map<String, Object>> details = (List<Map<String, Object>>) objData.get(detailKey);
                if (CollectionUtils.isNotEmpty(details)) {
                    resultMap.put("childrenvo", details);
                }
            }
        }
        dataMap.put("parentvo", resultMap);
        return dataMap;
    }

    /**
     * 格式化查询结果出参数据
     * @param allCount
     * @param retCount
     * @param billnum
     * @param result
     * @param status
     * @param errorMsg
     * @return
     * @throws Exception
     */
    public static Map<String,Object> formatOutputParam(int allCount, int retCount, String billnum, List<Map<String, Object>> result, String status, String errorMsg) throws Exception {
        if(CollectionUtils.isNotEmpty(result)) {
            if ("productionOrder".equals(billnum)) {
                // 生产订单特殊处理--需要拍平表体（取第一个表体）
                result = transProductOrderListMap(result);
            } else if ("pickingRequisition".equals(billnum)) {
                // 出库申请单特殊处理--表头需要取表体的upcode字段
                result = transPickingRequisitionListMap(result);
            }
            if(!"stockInfo".equals(billnum)) {
                for (Map<String, Object> item : result) {
                    if (item.containsKey("parentvo")) {
                        Map<String, Object> data = (Map<String, Object>) item.get("parentvo");
                        data = ExchangeUtil.getsingleMap4EJia(billnum, data);
                        item.put("parentvo", data);
                    }
                }
            }
        }
        Map<String,Object> dataMap = new HashMap<>();
        dataMap.put("allcount", allCount);
        dataMap.put("retcount", retCount);
        dataMap.put("datas", result);
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("status", status);
        resultMap.put("data",dataMap);
        resultMap.put("errormsg", errorMsg);
        return resultMap;
    }

    private static List<Map<String, Object>> transPickingRequisitionListMap(List<Map<String, Object>> result) {
        List<Map<String, Object>> bills = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> data : result) {
            Map<String, Object> bill = transPickingRequisitionDetailMap(data);
            bills.add(bill);
        }
        return bills;
    }

    private static Map<String, Object> transPickingRequisitionDetailMap(Map<String, Object> data) {
        Map<String, Object> parentvo = (Map<String, Object>) data.get("parentvo");
        if(null!=parentvo && null!=parentvo.get("childrenvo")){
            List<Map<String, Object>> details = (List<Map<String, Object>>)parentvo.get("childrenvo");
            if(CollectionUtils.isNotEmpty(details)){
                parentvo.put("upcode", details.get(0).get("upcode"));
            }

        }
        return data;
    }

    public static List<Map<String, Object>> transProductOrderListMap(List<Map<String, Object>> datas) throws Exception {
        List<Map<String, Object>> bills = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> data : datas) {
            Map<String, Object> bill = transProductOrderDetailMap(data);
            bills.add(bill);
        }
        return bills;
    }
    /**
     * 把生产订单表体信息拍平和表头一起返回
     * @param productOrderInfo 生产订单信息
     * @return 拍平后的生产订单表体信息拍平和表头一起返回
     */
    public static Map<String, Object> transProductOrderDetailMap(Map<String, Object> productOrderInfo) {

        Map<String, Object> destMap = new HashMap<String, Object>(productOrderInfo);
        Map<String, Object> parentvo = (Map<String, Object>) destMap.get("parentvo");
        List<Map<String, Object>> orderProductList = (List<Map<String, Object>>) parentvo.get("childrenvo");
        parentvo.remove("childrenvo");
        //特征值 ZZKH01_name 最终客户名称
        if(parentvo.containsKey("defineDts")){
            Map<String, Object> defineDts = (Map<String, Object>) parentvo.get("defineDts");
            parentvo.put("customerName",defineDts.get("ZZKH01_name"));
        }
        if (orderProductList != null && !orderProductList.isEmpty()) {
            // 只取第一个行信息
            Map<String, Object> orderProduct = orderProductList.get(0);
            parentvo.put("batchNo", orderProduct.get("batchNo"));
            parentvo.put("productId",orderProduct.get("productId"));
            parentvo.put("productCode",orderProduct.get("productCode"));
            parentvo.put("productName",orderProduct.get("productName"));
            parentvo.put("quantity",orderProduct.get("quantity"));
            parentvo.put("versionCode",orderProduct.get("versionCode"));//BOM版本
            parentvo.put("bomId",orderProduct.get("bomId"));
            parentvo.put("startDate",orderProduct.get("startDate"));
            parentvo.put("finishDate",orderProduct.get("finishDate"));
            //parentvo.put("projectCode",orderProduct.get("projectCode"));
            //parentvo.put("projectName",orderProduct.get("projectName"));
            parentvo.put("upcode",orderProduct.get("upcode"));
            parentvo.put("bodyid",orderProduct.get("id"));
            parentvo.put("firstupcode",orderProduct.get("firstupcode"));
            //特征值
            if(orderProduct.containsKey("productDefineDts")){
                Map<String, Object> defineDts = (Map<String, Object>) orderProduct.get("productDefineDts");
                //采购订单号
                parentvo.put("cgddh",defineDts.get("cgddh"));
                //对方订单行号
                parentvo.put("SC01",defineDts.get("SC01"));
                //2026-07-01 最终客户在表体了
                parentvo.put("customerName",defineDts.get("ZZKH01_name"));
            }
            //20260627:正式环境增加存量敏感特征
            if(orderProduct.containsKey("freeCharacteristics")){
                parentvo.put("freeCharacteristics", orderProduct.get("freeCharacteristics"));
            }
        }
        return destMap;
    }

}
