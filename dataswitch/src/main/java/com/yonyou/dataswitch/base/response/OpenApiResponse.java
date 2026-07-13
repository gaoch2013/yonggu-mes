package com.yonyou.dataswitch.base.response;

import lombok.Data;
import lombok.SneakyThrows;
import com.yonyou.dataswitch.base.exception.BusinessException;

/**
 * 本类主要用于
 */
@Data
public class OpenApiResponse<T>{

    private Object code;

    private String message;

    private T data;

    @SneakyThrows
    public void check() {
        if (code instanceof Number && ((int) code == 0 || (int) code == 200)) {
            return;
        }

        if ("00000".equals(code) || "200".equals(code)) {
            return;
        }
        throw new BusinessException(message);
    }
}
