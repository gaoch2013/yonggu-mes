package com.yonyou.dataswitch.base.properties;


import com.yonyou.dataswitch.base.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author yonyou
 */
public interface URLProperties {

    char URL_SPLIT_CHAT = '/';

    default Set<String> parseCommaSplitParam(String source) {

        if (StringUtils.isBlank(source)) {
            return Collections.emptySet();
        }

        String[] split = source.split(",");

        return Stream.of(split).map(String::trim).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
    }

    default StringBuilder concatUrl(String urlPrefix, String... urlFragment) {
        if (urlPrefix == null) {
            throw new BusinessException("invalid url prefix, can not be null");
        }
        StringBuilder builder = new StringBuilder(urlPrefix);

        for (String fragment : urlFragment) {
            concatURLFragment(builder, fragment);
        }
        return builder;
    }


    /**
     * 连接地址和参数，主要解决？&拼接问题
     *
     * @param path     地址
     * @param paramUrl 参数
     * @return
     */
    default String concatParam(String path, String paramUrl) {

        if (path.indexOf('?') == -1) {
            if (paramUrl.indexOf('?') == -1) {
                return path + "?" + paramUrl;
            }
            return path + paramUrl;
        }

        if (paramUrl.startsWith("&")) {
            if (path.endsWith("&")) {
                return path.substring(0, path.length() - 1) + paramUrl;
            }
            return path + paramUrl;
        }

        if (path.endsWith("&")) {
            return path + paramUrl;
        } else {
            return path + "&" + paramUrl;
        }

    }

    default String concatURL(String urlPrefix, String... urlFragment) {
        return concatUrl(urlPrefix, urlFragment).toString();
    }

    default void concatURLFragment(StringBuilder builder, String fragment) {

        if (StringUtils.isBlank(fragment)) {
            return;
        }

        if (fragment.length() == 1 && fragment.charAt(0) == URL_SPLIT_CHAT) {
            return;
        }

        String trimFragment = fragment.trim();

        //参数之后
        if (builder.indexOf("?") != -1) {
            builder.append(trimFragment);
            return;
        }

        if (trimFragment.startsWith("?")) {
            builder.append(trimFragment);
            return;
        }

        concatPaths(builder, trimFragment);
    }

    default void concatPaths(StringBuilder builder, String trimFragment) {
        char c1 = builder.charAt(builder.length() - 1);

        char c2 = trimFragment.charAt(0);

        if (c1 == URL_SPLIT_CHAT && c2 == URL_SPLIT_CHAT) {
            builder.append(trimFragment.substring(1));
            return;
        }

        if (c1 == URL_SPLIT_CHAT || c2 == URL_SPLIT_CHAT) {
            builder.append(trimFragment);
            return;
        }

        builder.append(URL_SPLIT_CHAT).append(trimFragment);
    }

    default String buildQueryString(Map<String, String> params, boolean doUrlEncode) {
        StringBuilder builder = new StringBuilder();
        params.forEach((k, v) -> {
            if (StringUtils.isNotBlank(v)) {
                builder.append(k).append('=');
                if (doUrlEncode) {
                    try {
                        builder.append(URLEncoder.encode(v, StandardCharsets.UTF_8.name()));
                    } catch (UnsupportedEncodingException e) {
                        //ignore
                        e.printStackTrace();
                    }
                } else {
                    builder.append(v);
                }
                builder.append("&");
            }
        });
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

}
