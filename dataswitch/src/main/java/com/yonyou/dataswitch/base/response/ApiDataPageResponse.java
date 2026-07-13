package com.yonyou.dataswitch.base.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @description: 分页接口响应数据解析
 * @author: yonyou
 **/
@Data
public class ApiDataPageResponse {

    private Integer pageIndex;

    private Integer pageSize;

    private Integer pageCount;

    private Integer beginPageIndex;

    private Integer endPageIndex;

    private Integer recordCount;

    private List<Map<String, Object>> recordList;
}
