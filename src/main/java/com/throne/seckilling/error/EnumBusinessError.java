package com.throne.seckilling.error;

/**
 * 各类错误的枚举类型
 * 错误码 错误信息
 * com.throne.seckilling.error
 * Created by throne on 2020/4/11
 */
public enum EnumBusinessError implements CommonError {
    // 各种场景下的参数校验错误
    PARAMETER_VALIDATION_ERROR(99999, "参数校验错误"),

    // 用户相关错误 10开头
    USER_NOT_EXISTS(10001, "用户不存在"),
    USER_LOGIN_FAILED(10002, "用户名或密码错误"),
    USER_PARAM_ERROR(10003, "用户提交参数错误"),
    USER_NOT_LOGIN(10004, "用户尚未登录"),

    // 商品相关错误 20开头
    ITEM_NOT_EXISTS(20001, "所查询商品不存在"),
    NOT_ENOUGH_STOCK(20001, "商品库存不足"),
    // 未知错误
    UNKNOWN_ERROR(88888, "未知错误"),

    ;
    private Integer errorCode;
    private String errorMessage;

    EnumBusinessError(Integer errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public Integer getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getErrorMsg() {
        return this.errorMessage;
    }

    @Override
    public CommonError setErrorMsg(String errorMsg) {
        this.errorMessage = errorMsg;
        return this;
    }
}
