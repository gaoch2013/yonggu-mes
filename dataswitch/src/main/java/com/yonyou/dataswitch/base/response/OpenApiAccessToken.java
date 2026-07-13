package com.yonyou.dataswitch.base.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 本类主要用于
 *
 * @author yonyou
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenApiAccessToken {

    private String accessToken;

    private Long expiredAt;

    @Data
    public static class OpenApiAccessTokenDTO {

        private String access_token;
        private Long expire;

        public OpenApiAccessToken build() {
            try {
                access_token = URLEncoder.encode(access_token, StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return new OpenApiAccessToken(access_token, System.currentTimeMillis() + expire * 1000);
        }

    }

    public boolean expired() {
        return expiredAt != null && System.currentTimeMillis() + 1000 < expiredAt;
    }

}
