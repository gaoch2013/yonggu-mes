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
public class OthOutRecordControllerTest {

    @Resource
    private OthOutRecordController othOutRecordController;


    @Test
    public void test_getOthOutRecordInfo() throws Exception {
        //构造参数
        Map<String, Object> inputParams = new HashMap<>();
        Map<String,Object> indata = new HashMap<>();
        indata.put("cbillcode","QTCK20260417000001");
        inputParams.put("indata", indata);

        try {
            String result = othOutRecordController.getOthOutRecordInfo(inputParams);
        } catch (NullPointerException e) {
        }
    }
    @Test
    public void test_saveOthOutRecordInfo() throws Exception {
        //新增保存
        //构造参数
        Map<String,Object> parentvo = new HashMap<>();
        parentvo.put("cdptid","01503");
        parentvo.put("cwhsmanagerid","YG0017");
        parentvo.put("coperatorid",null);
        parentvo.put("cwarehouseid","03");
        parentvo.put("orgCode","1001");
        parentvo.put("cdispatcherid","A10001");
        parentvo.put("vouchdate","2026-07-01 00:00:00");
        parentvo.put("accountOrg","1001");
        parentvo.put("_status","Insert");
        List<Map<String,Object>> childrenvos = new ArrayList<>();
        Map<String,Object> childrenvo = new HashMap<>();
        childrenvo.put("cinventoryid","050100027");
        childrenvo.put("noutnum","10");
        childrenvo.put("vbatchcode","8218FS");
        childrenvo.put("_status","Insert");
        childrenvos.add(childrenvo);
        parentvo.put("othOutRecords",childrenvos);
        Map<String, Object> inputParams = new HashMap<>();
        inputParams.put("indata", parentvo);

        try {
            String result = othOutRecordController.saveOthOutRecordInfo(inputParams);
        } catch (NullPointerException e) {
        }
    }
}