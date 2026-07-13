package com.yonyou.dataswitch.controller;

import com.yonyou.dataswitch.base.utils.JacksonUtils;
import com.yonyou.dataswitch.controller.util.ParamUtil;
import com.yonyou.dataswitch.service.OrgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对外接口：产品相关controller
 */
@Slf4j
@ResponseBody
@RestController
@RequestMapping("/dev/org/v1")
public class OrgController {

    @Resource
    OrgService orgService;

    /**
     * @param inputParams 查询参数
     * @return 返回success表示成功接收，抛出异常等其他代表接收失败
     */
    @PostMapping("/getOrgInfo")
    public String getOrgInfo(@RequestBody Map<String,Object> inputParams) throws Exception {
        //请求地址：http://localhost:8833/dev/org/v1/getOrgInfo
        //body:
        log.info("getOrgInfo--inputParams:{}",JacksonUtils.toJSONString(inputParams));
        String status = "success";
        String errorMsg = "";
        List<Map<String, Object>> result = new ArrayList<>();
        int allCount = 0;
        int retcount = 0;
        try {
            //先查列表数据
            Map<String, Object> qryParams = getQryParams(inputParams);
            List<Map<String, Object>>  listresult = orgService.list(qryParams);
            log.info("getOrgInfo--listresult:{}",JacksonUtils.toJSONString(listresult));
            //再查详情数据：批量详情查询最多支持10条数据
             if(!CollectionUtils.isEmpty(listresult)){
                 allCount = listresult.size();
                 retcount = allCount;
                 List<Map<String,Object>> allDetails = new ArrayList<>();
                 for(Map<String, Object> tmpresult : listresult){
                     Map<String,String> map = new HashMap<>();
                     map.put("id",tmpresult.get("id").toString());
                     Map<String, Object> detail =  orgService.detail(map);
                     if(!CollectionUtils.isEmpty(detail)){
                         allDetails.add(detail);
                     }
                 }
                 result = allDetails;
             }
        }catch (Exception e){
            log.error("getOrgInfo error:{}",e.getMessage(),e);
            errorMsg = e.getMessage();
            status = "error";
        }
        Map<String, Object> resultMap = ParamUtil.formatOutputParam(allCount, retcount,"org", result, status, errorMsg);

        log.info("getOrgInfo--resultMap:{}",JacksonUtils.toJSONString(resultMap));
        return JacksonUtils.toJSONString(resultMap);
    }

    /**
     * 构造查询参数
     * @param params
     * @return
     */
    private static Map<String, Object> getQryParams(Map<String, Object> params) throws Exception {
        Map<String,Object> inputParams = ParamUtil.getindata(params);
        //根据外系统传参构造列表查询参数
        Map<String,Object> qryParams = new HashMap<>();
        qryParams.put("pageIndex", inputParams.getOrDefault("page_now", 1));
        qryParams.put("pageSize", inputParams.getOrDefault("page_size", 100));
        qryParams.put("isSum", true);//固定条件
        qryParams.put("code", inputParams.get("code"));
        return qryParams;
    }

}