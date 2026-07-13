package com.yonyou.dataswitch.base.datacenter;


import com.yonyou.dataswitch.base.datacenter.pojo.GatewayAddressResponse;

/**
 * @author yonyou
 * @description: TODO 获取租户所在数据中心域名
 */
public interface DataCenterUrlProvider {

    //租户所在数据中心的核心网关域名，调用业务接口时需要拼接核心网关域名和接口相对路径
    String buildBusinessUrl();
    //租户所在数据中心的auth域名，调用获取token接口时，需要拼接auth域名和获取token接口相对路径
    String buildTokenUrl();
    //获取租户所在数据中心域名
    GatewayAddressResponse.GatewayAddressDTO queryGatewayAddress();
    //查询租户id
    String queryTenantId();
}
