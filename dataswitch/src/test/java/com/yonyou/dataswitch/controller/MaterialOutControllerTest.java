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
public class MaterialOutControllerTest {

    @Resource
    private MaterialOutController materialOutController;


    @Test
    public void test_getMaterialOutInfo() throws Exception {
        //构造参数
        Map<String, Object> inputParams = new HashMap<>();
        Map<String,Object> indata = new HashMap<>();
        indata.put("sourcebillcode","CKSQ20260403000001");
        inputParams.put("indata", indata);

        try {
            String result = materialOutController.getMaterialOutInfo(inputParams);
        } catch (NullPointerException e) {
        }
    }
    @Test
    public void test_saveMaterialOutInfo() throws Exception {
        //构造参数
        Map<String, Object> inputParams = new HashMap<>();
        Map<String,Object> indata = new HashMap<>();
        indata.put("sourcebillcode","CKSQ20260624000002");
        List<Map<String,Object>> productinfo = new ArrayList<>();
        Map<String,Object> productinfo1 = new HashMap<>();
//        productinfo1.put("product","050100027");
//        productinfo1.put("qty",200);
//        productinfo1.put("batchno","8218FS");
//        productinfo.add(productinfo1);

        productinfo1 = new HashMap<>();
        productinfo1.put("product","0200051");
        productinfo1.put("qty",50);
        productinfo1.put("batchno","260422");
        productinfo.add(productinfo1);
//        productinfo1 = new HashMap<>();
//        productinfo1.put("product","0001000003");
//        productinfo1.put("qty",2);
//        productinfo1.put("batchno","20260422");
//        productinfo1.put("goodsposition","000001");
//        productinfo.add(productinfo1);
        indata.put("productinfo", productinfo);
        inputParams.put("indata", indata);
        try {
            String result = materialOutController.saveMaterialOutInfo(inputParams);
        } catch (NullPointerException e) {
        }
    }
}