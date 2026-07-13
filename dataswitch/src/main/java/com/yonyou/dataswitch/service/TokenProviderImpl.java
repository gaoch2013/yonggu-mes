package com.yonyou.dataswitch.service;

import com.yonyou.dataswitch.base.auth.token.OpenApiTokenCacheProvider;
import com.yonyou.dataswitch.base.datacenter.impl.DataCenterUrlProviderImpl;
import com.yonyou.dataswitch.base.datacenter.pojo.GatewayAddressResponse;
import com.yonyou.dataswitch.base.response.OpenApiAccessToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


/**
 * @author yonyou
 */
@Slf4j
@Component
public class TokenProviderImpl extends DataCenterUrlProviderImpl implements OpenApiTokenCacheProvider {

    private static final String TOKEN_CACHE_KEY = "openapi:accesstoken";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${ucf.mdd.open-api.tenantId:}")
    private String tenantId;

    /**
     * TODO：不建议每次都调用接口，建议将租户地址对应关系持久化到数据库和缓存中
     * @return GatewayAddressResponse.GatewayAddressDTO
     */
    @Override
    public GatewayAddressResponse.GatewayAddressDTO queryGatewayAddress() {
        return super.queryGatewayAddress();
    }

    /**
     *  从缓存中获取token
     * @return token对象
     */
    @Override
    public OpenApiAccessToken loadTokenFromCache() {
        String cacheKey = buildCacheKey();
        Object cachedToken = redisTemplate.opsForValue().get(cacheKey);
        if (cachedToken != null) {
            log.info("从Redis缓存加载token, tenantId={}", tenantId);
            return (OpenApiAccessToken) cachedToken;
        }
        log.debug("Redis中无缓存token, tenantId={}", tenantId);
        return null;
    }

    /**
     *  将token缓存到Redis
     * @param token token实体
     */
    @Override
    public void saveTokenToCache(OpenApiAccessToken token) {
        if (token == null) {
            return;
        }
        String cacheKey = buildCacheKey();
        // 计算剩余过期时间
        long ttl = token.getExpiredAt() - System.currentTimeMillis();
        if (ttl > 0) {
            redisTemplate.opsForValue().set(cacheKey, token, ttl, TimeUnit.MILLISECONDS);
            log.info("token已缓存到Redis, tenantId={}, ttl={}ms", tenantId, ttl);
        } else {
            log.warn("token已过期，不进行缓存, tenantId={}", tenantId);
        }
    }

    private String buildCacheKey() {
        return TOKEN_CACHE_KEY + ":" + tenantId;
    }

}
