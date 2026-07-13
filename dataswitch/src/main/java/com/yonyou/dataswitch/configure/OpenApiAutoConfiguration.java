package com.yonyou.dataswitch.configure;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.yonyou.dataswitch.base.auth.TenantAuthProvider;
import com.yonyou.dataswitch.base.auth.impl.TenantAuthProviderImpl;
import com.yonyou.dataswitch.base.auth.network.OpenApiRequestEncrypt;
import com.yonyou.dataswitch.base.auth.token.OpenApiTokenCacheProvider;
import com.yonyou.dataswitch.base.datacenter.DataCenterUrlProvider;
import com.yonyou.dataswitch.base.datacenter.pojo.GatewayAddressResponse;
import com.yonyou.dataswitch.base.event.ReceivedEventDispatcher;
import com.yonyou.dataswitch.base.event.impl.ReceivedEventDispatcherImpl;
import com.yonyou.dataswitch.base.properties.OpenApiProperties;
import com.yonyou.dataswitch.base.response.OpenApiAccessToken;
import com.yonyou.dataswitch.service.TokenProviderImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.TimeUnit;

/**
 * 注入
 */
@RequiredArgsConstructor
@Configuration
@EnableConfigurationProperties(OpenApiProperties.class)
public class OpenApiAutoConfiguration {

    private final OpenApiProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public OpenApiRequestEncrypt openApiRequestEncrypt() {
        return new OpenApiRequestEncrypt();
    }

    @Bean
    public TenantAuthProvider tenantAuthProvider(OpenApiRequestEncrypt encrypt) {
        return new TenantAuthProviderImpl(properties, encrypt);
    }

    @Bean
    @ConditionalOnMissingBean
    public ReceivedEventDispatcher receivedEventDispatcher(ApplicationContext context) {
        return new ReceivedEventDispatcherImpl(context);
    }

    @Bean
    @ConditionalOnMissingBean
    public DataCenterUrlProvider dataCenterUrlProvider() {
        return new TokenProviderImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public OpenApiTokenCacheProvider openApiTokenCacheProvider() {
        return new TokenProviderImpl();
    }


    @Bean("moreDataUrlCache")
    public Cache<String, GatewayAddressResponse.GatewayAddressDTO> moreDataUrlCache(){
        return Caffeine.newBuilder()
                //最后一次读或写操作后经过指定时间过期
                .expireAfterAccess(30, TimeUnit.DAYS)
                .initialCapacity(100)
                .maximumSize(200)
                .build();
    }

    @Bean("tokenCache")
    public Cache<String, OpenApiAccessToken> tokenCache(){
        return Caffeine.newBuilder()
                //最后一次读或写操作后经过指定时间过期
                .expireAfterAccess(2, TimeUnit.HOURS)
                .initialCapacity(100)
                .maximumSize(200)
                .build();
    }

}
