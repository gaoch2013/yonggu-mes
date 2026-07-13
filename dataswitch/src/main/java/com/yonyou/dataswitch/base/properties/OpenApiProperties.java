package com.yonyou.dataswitch.base.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 本类主要用于
 *
 * @author yonyou
 */
@Data
@ConfigurationProperties(prefix = OpenApiProperties.UNIVERSAL_PREFIX + ".open-api")
public class OpenApiProperties implements ConnectionProperties, URLProperties {

    public static final String UNIVERSAL_PREFIX = "ucf.mdd";

    private String gatewayAddressUrl;

    private String appCode;

    private String appKey;

    private String appSecret;

    private String tenantId;

    private String ssoYhtUrl;

    private String thirdUcId;

    private String gatewayUrl;

    private String tokenUrl;

    private String tenantCode;
}
