package com.yonyou.dataswitch.base.properties;

/**
 * 链接属性
 *
 * @author yonyou
 */
public interface ConnectionProperties {

    /**
     * 获取连接超时时间,毫秒
     *
     * @return 连接超时时间
     */
    default Integer getConnectTimeoutMs() {
        return 2000;
    }

    /**
     * 获取连接读取超时,毫秒
     *
     * @return 超时时间
     */
    default Integer getReadTimeoutMs() {
        return 30000;
    }

    /**
     * 获取连接池连接最大数量
     *
     * @return 连接最大数量
     */
    default Integer getMaxTotal() {
        return 2000;
    }

    /**
     * 获取
     * @return
     */
    default Integer getDefaultMaxPerRoute() {
        return 1500;
    }

    default Integer getConnectionRequestTimeout() {
        return 2000;
    }

}
