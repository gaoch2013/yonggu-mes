package com.yonyou.dataswitch.base.event.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * @author nishch
 * @description: 解析事件订阅响应数据
 * @date 2023/8/2
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Event implements Serializable {

    private String type;

    private Long timestamp;

    private String eventId;

    private String suiteKey;

    private String content;

}
