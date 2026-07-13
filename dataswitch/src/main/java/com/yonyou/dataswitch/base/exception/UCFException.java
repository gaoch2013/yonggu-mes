package com.yonyou.dataswitch.base.exception;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author yonyou
 */
public abstract class UCFException extends RuntimeException {

    public UCFException(String message) {
        super(message);
    }

    public UCFException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 错误码,分配详情见
     *
     * @return 错误码
     */
    public abstract int getCode();

    /**
     * 获取http的错误code
     *
     * @return http错误码
     */
    public final int getHttpCode() {

        int code = getCode();

        if (code > 9999999) {
            return code / 100000;
        }

        if (code > 999999) {
            return code / 10000;
        }

        if (code > 99999) {
            return code / 1000;
        }

        if (code > 9999) {
            return code / 100;
        }

        if (code > 1000) {
            return code / 10;
        }
        return code;
    }

    public Map<Locale, String> getDisplayMessage() {
        Map<Locale, String> message = new HashMap<>(3);

        if (getHttpCode() > 499) {
            message.put(Locale.SIMPLIFIED_CHINESE, "系统发生错误");
            message.put(Locale.TRADITIONAL_CHINESE, "系統發生錯誤");
            message.put(Locale.US, "System Error.");
        } else if (getHttpCode() > 399) {
            message.put(Locale.SIMPLIFIED_CHINESE, "参数错误");
            message.put(Locale.TRADITIONAL_CHINESE, "參數錯誤");
            message.put(Locale.US, "Parameter Invalid.");
        }

        return message;
    }


}
