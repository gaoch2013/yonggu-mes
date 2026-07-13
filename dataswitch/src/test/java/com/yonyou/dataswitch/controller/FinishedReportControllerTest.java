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
public class FinishedReportControllerTest {

    @Resource
    private FinishedReportController finishedReportController;


    @Test
    public void test_saveFinishedReportInfo() throws Exception {
        //构造参数
        Map<String, Object> inputParams = new HashMap<>();
        Map<String, Object> orderInfo = new HashMap<>();
        orderInfo.put("mocode", "SCDD20260701000003");
        orderInfo.put("orgCode", "1001");
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> productinfo = new HashMap<>();
        //productinfo.put("product", "0006000005");
        productinfo.put("qty", "599.90000");
        productinfo.put("batchno", "5101GS");
        productinfo.put("produceDate", "2026-07-01");
        //productinfo.put("expirationDate", "2027-06-27");
        list.add(productinfo);
        orderInfo.put("productinfo", list);
        inputParams.put("indata", orderInfo);

        try {
            String result = finishedReportController.saveFinishedReportInfo(inputParams);
        } catch (NullPointerException e) {
        }

    }
}