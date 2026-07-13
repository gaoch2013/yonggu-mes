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
public class BomControllerTest {

    @Resource
    private BomController bomController;


    @Test
    public void test_getBomInfo() throws Exception {
        //构造查询参数
        Map<String, Object> inputParams = new HashMap<>();
        Map<String, Object> indata = new HashMap<>();
        indata.put("invcode", "060100021");
        inputParams.put("indata", indata);
        try {
            String result = bomController.getBomInfo(inputParams);
        } catch (NullPointerException e) {
        }

    }
}