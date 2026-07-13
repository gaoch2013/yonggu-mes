package com.yonyou.dataswitch.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class StoreProRecordControllerTest {

    @Resource
    private StoreProRecordController storeProRecordController;


    @Test
    public void test_getStoreProRecordInfo() throws Exception {
        //构造参数
        Map<String, Object> inputParams = new HashMap<>();
        Map<String,Object> indata = new HashMap<>();
        indata.put("vproductbatch","SCDD20260416000001");
        inputParams.put("indata", indata);

        try {
            String result = storeProRecordController.getStoreProRecordInfo(inputParams);
        } catch (NullPointerException e) {
        }
    }
    @Test
    public void test_saveStoreProRecordInfo() throws Exception {
        //构造参数
        Map<String, Object> inputParams = new HashMap<>();
        Map<String,Object> indata = new HashMap<>();
        indata.put("mocode","SCDD20260624000002");
        List<Map<String,Object>> productinfo = new ArrayList<>();
        Map<String,Object> productinfo1 = new HashMap<>();
        productinfo1.put("product","060100415");
        productinfo1.put("qty",0);
        productinfo.add(productinfo1);

        indata.put("productinfo", productinfo);
        inputParams.put("indata", indata);

        try {
            String result = storeProRecordController.saveStoreProRecordInfo(inputParams);
        } catch (NullPointerException e) {
        }
    }
}