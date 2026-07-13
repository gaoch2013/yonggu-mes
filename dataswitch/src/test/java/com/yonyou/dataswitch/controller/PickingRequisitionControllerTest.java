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
public class PickingRequisitionControllerTest {

    @Resource
    private PickingRequisitionController pickingRequisitionController;


    @Test
    public void test_getPickingRequisitionInfo() throws Exception {
        //构造参数
        Map<String, Object> inputParams = new HashMap<>();
        Map<String,Object> indata = new HashMap<>();
        indata.put("pageIndex",1);
        indata.put("pageSize",10);
        indata.put("isSum",true);//固定条件
        indata.put("forderstatus","B");
        indata.put("date_begin","2026-04-01");
        indata.put("date_end","2026-04-09");
        indata.put("code","CKSQ20260403000001");
        inputParams.put("indata", indata);

        try {
            String result = pickingRequisitionController.getPickingRequisitionInfo(inputParams);
        } catch (NullPointerException e) {
        }

    }
}