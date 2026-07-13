package com.yonyou.dataswitch.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.yonyou.dataswitch.base.event.pojo.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description: 事件解析实体
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ISVEventExtendDto extends Event {

    private String definitionVariable;

    /**
     * 变动的 staff id
     **/
    private String[] staffId;

    /**
     * 变动的 dept id
     **/
    private String[] deptId;

    /**
     * 变动的 user id
     */
    private String[] userId;

    private String actionKey;

    private Object value;

    private String tenantId;

}
