package com.throne.seckilling.validator;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

/**
 * 用户存放数据校验结果的类
 */
public class ValidationResult {
    private boolean error = false;
    private HashMap<String, String> resultMap = new HashMap<>();

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public HashMap<String, String> getResultMap() {
        return resultMap;
    }

    public void setResultMap(HashMap<String, String> resultMap) {
        this.resultMap = resultMap;
    }

    /**
     *
     * @return 返回全部的错误信息，以逗号分隔
     */
    public String getErrorMsg(){
        return StringUtils.join(this.resultMap.values(), ",");
    }
}
