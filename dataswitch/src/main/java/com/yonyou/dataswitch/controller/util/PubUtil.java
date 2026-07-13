package com.yonyou.dataswitch.controller.util;

import com.yonyou.dataswitch.base.utils.JacksonUtils;
import com.yonyou.dataswitch.controller.BatchnoController;
import com.yonyou.dataswitch.controller.ProductController;
import com.yonyou.dataswitch.service.StockService;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Component
public class PubUtil {
    @Resource
    ProductController productController;
    @Resource
    private BatchnoController batchnoController;
    @Resource
    private StockService stockService;

    public Map<String, Object> getBatchNoInfoByProduct(Object productId, String batchno, String productCode) throws Exception {
        Map<String, Object> qryBatchParams = new HashMap<>();
        Map<String, Object> qrybatchdata = new HashMap<>();
        qrybatchdata.put("product", productId);
        qrybatchdata.put("batchno", batchno);
        qryBatchParams.put("indata", qrybatchdata);
        String controllerBatchnoInfo = batchnoController.getBatchnoInfo(qryBatchParams);
        Map<String, Object> batchnoMap = JacksonUtils.toMap(controllerBatchnoInfo);
        if (CollectionUtils.isEmpty(batchnoMap)) {
            throw new Exception("未查询到批次信息，物料编码=" + productCode + ",批次号=" + batchno);
        }
        Map<String, Object> batchnodataMap = (Map<String, Object>) batchnoMap.get("data");
        List<Map<String, Object>> batchnodatasMap = (List<Map<String, Object>>) batchnodataMap.get("datas");
        if (CollectionUtils.isEmpty(batchnodatasMap)) {
            throw new Exception("未查询到批次信息，物料编码=" + productCode + ",批次号=" + batchno);
        }
        Map<String, Object> batchnodata = batchnodatasMap.get(0);
        return batchnodata;
    }

    public  Map<String, Object> getProductInfoByCode(String productCode) throws Exception {
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
    public Map<String,Object> getCurrentStockCharacteristic(String cwhcode,String productCode,String batchno) throws Exception {
        Map<String,Object> qryParams = new HashMap<>();
        if(!StringUtils.isEmpty(cwhcode)){
            qryParams.put("warehouse.code", cwhcode);
        }
        if(!StringUtils.isEmpty(productCode)){
            qryParams.put("productn.code", productCode);
        }
        if(!StringUtils.isEmpty(batchno)){
            qryParams.put("batchno", batchno);
        }

        List<Map<String, Object>> stockList = stockService.stockQuery(qryParams);
        if(null!=stockList && !CollectionUtils.isEmpty(stockList)){
            for(Map<String, Object> stock : stockList){
                if(stock.containsKey("currentStockCharacteristic")){
                    Map<String, Object> currentStockCharacteristic = (Map<String, Object>) stock.get("currentStockCharacteristic");
                    //if(currentStockCharacteristic.containsKey("BOMbanben_name")){
                        return currentStockCharacteristic;
                    //}
                }
            }
        }
        return null;
    }
}
