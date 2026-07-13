package com.yonyou.dataswitch.controller;

import com.yonyou.dataswitch.base.response.ApiDataPageResponse;
import com.yonyou.dataswitch.base.utils.JacksonUtils;
import com.yonyou.dataswitch.controller.util.ParamUtil;
import com.yonyou.dataswitch.service.BomService;
import com.yonyou.dataswitch.service.UnitService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 对外接口：BOM相关controller
 */
@Slf4j
@ResponseBody
@RestController
@RequestMapping("/dev/bom/v1")
public class BomController {

    @Resource
    BomService bomService;
    @Resource
    RoutingController routingController;
    @Resource
    UnitService unitService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    /**
     * @param inputParams 查询参数
     * @return 返回success表示成功接收，抛出异常等其他代表接收失败
     */
    @PostMapping("/getBomInfo")
    public String getBomInfo(@RequestBody Map<String,Object> inputParams) throws Exception {
        //请求地址：http://localhost:8833/dev/bom/v1/getBomInfo
        //body:
        log.info("getBomInfo--inputParams:{}",JacksonUtils.toJSONString(inputParams));
        String status = "success";
        String errorMsg = "";
        List<Map<String, Object>> result = new ArrayList<>();
        int allCount = 0;
        int retcount = 0;
        try {
            //先查列表数据
            Map<String, Object> qryParams = getQryParams(inputParams);
            ApiDataPageResponse pageResponse = bomService.list(qryParams);
            log.info("getBomInfo--listresult:{}",JacksonUtils.toJSONString(pageResponse));
            //再查详情数据：一条一条查询
             if(null!=pageResponse && !CollectionUtils.isEmpty(pageResponse.getRecordList())){
                 allCount = pageResponse.getRecordCount();
                 retcount = pageResponse.getRecordList().size();
                 List<Map<String,Object>> allDetails = new ArrayList<>();
                 for(Map<String, Object> tmpresult : pageResponse.getRecordList()){
                     Map<String,String> map = new HashMap<>();
                     map.put("id",tmpresult.get("id").toString());
                     Map<String, Object> detail =  bomService.detail(map);
                     if(!CollectionUtils.isEmpty(detail)){
                         //根据bom查工艺路线信息，bom上携带工艺路线信息
                         Map<String, Object> routingInfo = getRoutingInfo(detail);
                         allDetails.add(routingInfo);
                     }
                 }
                 result = allDetails;
             }
        }catch (Exception e){
            log.error("getBomInfo error:{}",e.getMessage(),e);
            errorMsg = e.getMessage();
            status = "error";
        }
        Map<String, Object> resultMap = ParamUtil.formatOutputParam(allCount, retcount,"bom", result, status, errorMsg);

        log.info("getBomInfo--resultMap:{}",JacksonUtils.toJSONString(resultMap));
        return JacksonUtils.toJSONString(resultMap);
    }

    /**
     * 查询工艺路线信息
     */
    private Map<String, Object> getRoutingInfo(Map<String, Object> detail) throws Exception {
        Map<String, Object> rtnmap = new HashMap<>();
        Map<String, Object> parventvoMap = new HashMap<>();
        List<Map<String, Object>> childrenvoMapList = new ArrayList<>();
        //查询工艺路线信息
        Map<String, Object> qryRoutingParams = new HashMap<>();
        Map<String, Object> qryParam1 = new HashMap<>();
        //根据BOM数据物料ID查询工艺路线
        qryParam1.put("productId", detail.get("productId"));
        qryRoutingParams.put("indata", qryParam1);
        String rtnInfo = routingController.getRoutingInfo(qryRoutingParams);
        Map<String, Object> resultMap = JacksonUtils.toMap(rtnInfo);
        if (CollectionUtils.isEmpty(resultMap)) {
            throw new Exception("未查询到已审核的工艺路线信息");
        }
        Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");
        List<Map<String, Object>> datasMap = (List<Map<String, Object>>) dataMap.get("datas");
        if (CollectionUtils.isEmpty(datasMap)) {
            throw new Exception("未查询到已审核的工艺路线信息");
        }
        parventvoMap.put("invcode", detail.get("productCode"));
        parventvoMap.put("version", detail.get("versionCode"));
        parventvoMap.put("sl", 1L);//和mes侧确认固定传1
        parventvoMap.put("sdate", detail.get("effectiveDate"));
        parventvoMap.put("edate", detail.get("expiryDate"));
        //工艺路线（一个bom只有一条工艺路线）
        Map<String, Object> routingMap = datasMap.get(0);
        //工艺路线的工序信息
        List<Map<String, Object>> routingOperationMapList = (List<Map<String, Object>>) routingMap.get("routingOperation");
        for (Map<String, Object> routingOperationMap : routingOperationMapList) {
            Map<String, Object> childrenvo = new HashMap<>();
            //zdy2 todo 上道工序产品名称：需要添加特征字段，参照档案。
            //特征值 本道工序输出物料编码
            if(routingOperationMap.containsKey("routingOperationDefineCharacter")){
                Map<String, Object> defineDts = (Map<String, Object>) routingOperationMap.get("routingOperationDefineCharacter");
                childrenvo.put("zdy2",defineDts.get("SCWL_code"));
            }
            //工序编码
            childrenvo.put("flbm", routingOperationMap.get("operationId_code").toString());
            //工序名称
            childrenvo.put("flmc", routingOperationMap.get("operationId_name").toString());
            //工序顺序号
            //返回的记录顺序号有小数位,如10.0；只取整数部分
            String xh = routingOperationMap.get("sn").toString();
            childrenvo.put("gyxh",(int) Math.floor(Double.parseDouble(xh)));
            //资源信息
            List<Map<String, Object>> resourceList = (List<Map<String, Object>>) routingOperationMap.get("routingOperationActivityType");
            //永固每道工序只有1个设备
            if (!CollectionUtils.isEmpty(resourceList)) {
                Map<String, Object> resourceMap = resourceList.get(0);
                childrenvo.put("gyzy_invcode", resourceMap.get("activityTypeId_code").toString());
                childrenvo.put("gyzy_invname", resourceMap.get("activityTypeId_name").toString());
            }
            //每道工序的投料信息
            List<Map<String, Object>> routingOperationComponentList = (List<Map<String, Object>>) routingOperationMap.get("routingOperationComponent");

            if (!CollectionUtils.isEmpty(routingOperationComponentList)) {
                List<Map<String, Object>> newRoutingOperationComponentList = new ArrayList<>();
                for (Map<String, Object> routingOperationComponent : routingOperationComponentList) {
                    Map<String, Object> newRoutingOperationComponent = new HashMap<>();
                    //数量
                    newRoutingOperationComponent.put("xhsl", routingOperationComponent.get("numeratorQuantity"));
                    //物料编码
                    newRoutingOperationComponent.put("invcode", routingOperationComponent.get("productCode"));
                    //主计量编码 根据id查编码
                    String unitId = routingOperationComponent.get("materialId_productId_unit").toString();
                    String unitCode="";
                    //先查缓存，没命中在调用接口查询
                    String cacheKey = "openapi:unit_code_id"+":"+unitId;
                    Object cachedUnitCode = redisTemplate.opsForValue().get(cacheKey);
                    if (cachedUnitCode != null) {
                        log.info("从Redis缓存加载单位编码, unitId={}", unitId);
                        unitCode=  (String) cachedUnitCode;
                    }else{
                        log.info("从调用接口加载单位编码, unitId={}", unitId);
                        Map<String, String> qryUnitmap = new HashMap<>();
                        qryUnitmap.put("id", unitId);
                        Map<String, Object> unitDetail = unitService.detail(qryUnitmap);
                        if (!CollectionUtils.isEmpty(unitDetail)) {
                            unitCode = unitDetail.get("code").toString();
                        }
                        if (!StringUtils.isEmpty(unitCode)) {
                            redisTemplate.opsForValue().set(cacheKey, unitCode, 3600, TimeUnit.SECONDS);
                            log.info("单位编码已缓存到Redis, unitCode={}, ttl={}s", unitCode, 3600);
                        } else {
                            log.warn("单位编码返回接口为空，不进行缓存, unitId={}", unitId);
                        }
                    }
                    newRoutingOperationComponent.put("zjldwmc", unitCode);
                    newRoutingOperationComponentList.add(newRoutingOperationComponent);
                }
                childrenvo.put("gyhl", newRoutingOperationComponentList);
            }else{
                childrenvo.put("gyhl", "[]");
            }
            childrenvoMapList.add(childrenvo);
        }
        rtnmap.put("parentvo", parventvoMap);
        rtnmap.put("childrenvo", childrenvoMapList);
        return rtnmap;
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
        if(inputParams.containsKey("version")){
            qryParams.put("versionCode", inputParams.get("version"));
        }else{
            //没有查具体版本号时取最新版本的数据：是否为最新版本：1:最新版本，0：全部版本
            qryParams.put("versionScope", 1);
        }
        if(inputParams.containsKey("invcode")) {
            if (inputParams.get("invcode") != null && !"".equals(inputParams.get("invcode"))) {
                String[] productCodeList = inputParams.get("invcode").toString().split(",");
                qryParams.put("productCodes", productCodeList);
            }
        }
        qryParams.put("isSum",true);//固定条件
        return qryParams;
    }

}