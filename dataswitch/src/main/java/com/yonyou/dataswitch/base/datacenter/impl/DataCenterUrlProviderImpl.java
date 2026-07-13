package com.yonyou.dataswitch.base.datacenter.impl;

import com.yonyou.dataswitch.base.datacenter.DataCenterUrlProvider;
import com.yonyou.dataswitch.base.datacenter.pojo.GatewayAddressResponse;
import com.yonyou.dataswitch.base.properties.OpenApiProperties;
import com.yonyou.dataswitch.base.utils.RequestUtil;
import lombok.SneakyThrows;

import javax.annotation.Resource;


/**
 * @author yonyou
 */
public abstract class DataCenterUrlProviderImpl implements DataCenterUrlProvider {

    @Resource
    public OpenApiProperties properties;

    @Override
    @SneakyThrows
    public GatewayAddressResponse.GatewayAddressDTO queryGatewayAddress() {
        //gaoch:如果在配置文件中无gatewayUrl和tokenUrl或者都为空，才调用接口从gatewayAddressUrl查询
        if(properties.getGatewayUrl() == null || properties.getTokenUrl() == null)
        {
            GatewayAddressResponse gatewayAddressResponse = RequestUtil.doGetType(properties.getGatewayAddressUrl().replace("%s", queryTenantId()), GatewayAddressResponse.class);
            return gatewayAddressResponse.getData();
        }else {
            return new GatewayAddressResponse.GatewayAddressDTO(properties.getGatewayUrl(), properties.getTokenUrl());
        }
    }

    @Override
    public String buildBusinessUrl() {
        return queryGatewayAddress().getGatewayUrl();
    }

    @Override
    public String buildTokenUrl() {
        return queryGatewayAddress().getTokenUrl();
    }


    @Override
    public String queryTenantId() {
        return properties.getTenantId();
    }
}
