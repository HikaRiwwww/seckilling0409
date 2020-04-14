package com.throne.seckilling.error;

/**
 * 对错误和异常进行统一规范的接口
 * com.throne.seckilling.error
 * Created by throne on 2020/4/11
 */
public interface CommonError {

    /**
     *
     * @return 返回错误码
     */
    public Integer getErrorCode();

    /**
     *
     * @return 返回错误信息
     */
    public String getErrorMsg();

    /**
     * 设置错误信息
     * @return 返回自身
     */
    public CommonError setErrorMsg(String errorMsg);
}
