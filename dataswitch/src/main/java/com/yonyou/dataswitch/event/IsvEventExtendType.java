package com.yonyou.dataswitch.event;


import com.yonyou.dataswitch.base.event.pojo.EventType;

/**
 * @description: 事件类型
 **/
public class IsvEventExtendType extends EventType {

    /**
     * 组织新增
     */
    public static final String BASE_ORG_EVENT_ADD_AFTER = "BASE_ORG_EVENT_ADD_AFTER";
    /**
     * 组织更新
     */
    public static final String BASE_ORG_EVENT_UPDATE_AFTE = "BASE_ORG_EVENT_UPDATE_AFTE";
    /**
     * 组织启用
     */
    public static final String BASE_ORG_EVENT_ENABLE_AFTE = "BASE_ORG_EVENT_ENABLE_AFTE";
    /**
     * 组织停用
     */
    public static final String BASE_ORG_EVENT_DISABLE_AFTE = "BASE_ORG_EVENT_DISABLE_AFTE";
    /**
     * 组织删除
     */
    public static final String BASE_ORG_EVENT_DELETE_AFTER = "BASE_ORG_EVENT_DELETE_AFTER";

}
