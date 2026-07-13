package com.yonyou.dataswitch.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ProductionOrderControllerTest {

    @Resource
    private ProductionOrderController productionOrderController;


    @Test
    public void test_getProductionOrderInfo() throws Exception {
        //构造参数
        Map<String, Object> inputParams = new HashMap<>();
        Map<String, Object> indata = new HashMap<>();
        indata.put("scddh", "SCDD20260701000001");
        inputParams.put("indata", indata);

        try {
            String result = productionOrderController.getProductionOrderInfo(inputParams);
        } catch (NullPointerException e) {
        }

    }

    @Test
    public void test_batchfinishWork() throws Exception {
        //构造参数
        Map<String, Object> inputParams = new HashMap<>();
        Map<String, Object> indata = new HashMap<>();
        indata.put("mocode", "SCDD20260701000001");
        inputParams.put("indata", indata);

        try {
            String result = productionOrderController.batchfinishWork(inputParams);
        } catch (NullPointerException e) {
        }

    }
}