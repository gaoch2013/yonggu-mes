package com.yonyou.dataswitch.controller.util;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;
import java.io.InputStream;
import java.io.InputStreamReader;
import lombok.extern.slf4j.Slf4j;
import java.util.*;

/**
 * 报文格式转换工具类
 *
 */
@Slf4j
public class ExchangeUtil {

    // 子表标识
    private static final String DETAILS = "childrenvo";
    // 孙表标识
    private static final String SNS = "sns";
    // NC孙表标识
    private static final String BARCODES = "barcodes";
    // 孙表子表关联关系KEY
    private static final String CSOURCEID = "csourcebid";

    private final static Map<String, String[]> propertyFiles = new HashMap<String, String[]>();
    static {
        //物料
        propertyFiles.put("product", new String[] {"product.properties"});
        // 生产订单
        propertyFiles.put("productionOrder",new String[] { "productionOrder.properties"});
        // 出库申请单
        propertyFiles.put("pickingRequisition", new String[] { "pickingRequisition.properties", "pickingRequisitionDetail.properties" });
        //材料出库单
        propertyFiles.put("materialOut", new String[] { "materialOut.properties","materialOutDetail.properties" });
        //产品入库单
        propertyFiles.put("storeProRecord", new String[] { "storeProRecord.properties","storeProRecords.properties" });
        //其他出库单
        propertyFiles.put("othOutRecord", new String[] { "othOutRecord.properties","othOutRecords.properties" });
    }
    public static String getDetailKey(String billnum) {
        switch (billnum) {
            case "storeProRecord":
                return "storeProRecords";
            case "othOutRecord":
                return "othOutRecords";
            case "materialOut":
                return "materOuts";
            case "productionOrder":
                return "orderProduct";
            case "pickingRequisition":
                return "requisitionDetail";
            case "product":
                return "detail";

            default:
                return "";
        }
    }

    /**
     * 转换报文格式为易加格式
     * @param billnum 报文类型
     * @param datas 报文数据
     * @return EJia格式报文数据
     */
    public static List<Map<String, Object>> getListMap4EJia(String billnum, List<Map<String, Object>> datas) throws Exception {
        List<Map<String, Object>> bills = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> data : datas) {
            Map<String, Object> bill = getsingleMap4EJia(billnum, data);
            bills.add(bill);
        }
        return bills;
    }
    public static Map<String, Object> getsingleMap4EJia(String billnum, Map<String, Object> data) throws Exception {
        return convertMapByProperty(billnum, data);
    }

    private static Map<String, Object> convertMapByProperty(String billnum, Map<String, Object> data) throws Exception {
        String[] propertyFiles = getPropertyFiles(billnum, data);
        if (propertyFiles == null) {
            return data;
        }
        String headPropertyFile="",detailPropertyFile="",snPropertyFile = "";
        if(propertyFiles.length > 0){
            headPropertyFile = propertyFiles[0];
            if(propertyFiles.length > 1){
                detailPropertyFile = propertyFiles[1];
                if (propertyFiles.length > 2) {
                    snPropertyFile = propertyFiles[2];
                }
            }
        }


        Map<String, Object> map = transMap(data, getProperty(headPropertyFile));
//        if (!map.containsKey("shopBusinessCode") && data.containsKey("shopBusinessCode")) {
//            map.put("shopBusinessCode",data.get("shopBusinessCode"))	;
//        }
        map.put("csourcetype", billnum);
        map.put("syscode", "YCTX");
        List<Map<String, Object>> details = new ArrayList<Map<String, Object>>();
        Map<String, List<Map<String, Object>>> sns = new HashMap<String, List<Map<String, Object>>>();
        if (data.containsKey(SNS)) {
            for (Map<String, Object> item : (List<Map<String, Object>>) data.get(SNS)) {
                Map<String, Object> sn = transMap(item, getProperty(snPropertyFile));
//                if (!sn.containsKey("shopBusinessCode") && item.containsKey("shopBusinessCode")) {
//                    sn.put("shopBusinessCode",item.get("shopBusinessCode"))	;
//                }
                List<Map<String, Object>> value = new ArrayList<Map<String, Object>>();
                String csourcebid = sn.containsKey(CSOURCEID)?sn.get(CSOURCEID).toString():"";
                if (sns.containsKey(csourcebid)) {
                    value = sns.get(csourcebid);
                }
                value.add(sn);
                sns.put(csourcebid, value);
            }
        }
        if (data.containsKey(DETAILS)) {
            for (Map<String, Object> item : (List<Map<String, Object>>) data.get(DETAILS)) {
                Map<String, Object> detail = transMap(item, getProperty(detailPropertyFile));
                String csourcebid = detail.containsKey(CSOURCEID)?detail.get(CSOURCEID).toString():"";
                if (sns.containsKey(csourcebid)) {
                    detail.put(BARCODES, sns.get(csourcebid));
                }
                details.add(detail);
            }
            map.put(DETAILS, details);
        }

        return map;
    }

    private static String[] getPropertyFiles(String billnum, Map<String, Object> data) {
        return propertyFiles.get(billnum);
    }
    public static Properties getProperty(String propertyFile) throws Exception {
        Properties configProperties = new Properties();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource resource = resolver.getResource("classpath:/properties/" + propertyFile);
        InputStream is = null;
        try {
            PropertiesPersister propertiesPersister = new DefaultPropertiesPersister();
            is = resource.getInputStream();
            propertiesPersister.load(configProperties, new InputStreamReader(is, "UTF-8"));
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return configProperties;
    }
    public static Map<String, Object> transMap(Map<String, Object> srcMap, Properties pro) {
        Map<String, Object> destMap = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : srcMap.entrySet()) {
            String srcKey = entry.getKey();
            if (srcKey.equals(DETAILS) || srcKey.equals(SNS))
                continue;
            if (pro.containsKey(srcKey)) {
                String destKey = pro.getProperty(srcKey);
                Object destValue = srcMap.get(srcKey);
                //log.info("destKey:"+destKey+",destValue:"+destValue);
                destMap.put(destKey, destValue);
            } else if (Objects.equals(srcKey,"shopBusinessCode")){
                destMap.put(srcKey, entry.getValue());
            }
            // 特殊处理
            srcKey = srcKey + "_1";
            if (pro.containsKey(srcKey)) {
                String destKey = pro.getProperty(srcKey);
                Object destValue = srcMap.get(srcKey);
                destMap.put(destKey, destValue);
            }
        }
        return destMap;
    }
}
