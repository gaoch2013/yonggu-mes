package com.yonyou.dataswitch.base.exception;

/**
 * @author yonyou
 */
public class BusinessException extends UCFException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public int getCode() {
        return UCFErrorCode.ILLEGAL_ARGUMENT;
    }



}
