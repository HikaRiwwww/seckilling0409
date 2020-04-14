package com.throne.seckilling.response;

/**
 * 定义一个通用的返回数据的类
 * 当该类被序列化后作为返回给前端时，呈现如下形式
 * {status: success; data:{key:value,...}}
 * com.throne.seckilling.response
 * Created by throne on 2020/4/11
 */
public class CommonReturnType {
    private String status;
    private Object data;

    /**
     * 获取一个默认status为success的响应类型
     * @param data 数据实体
     * @return
     */
    public static CommonReturnType create(Object data){
        return CommonReturnType.create("success", data);
    }

    /**
     * 根据传入的status和data参数构建一个响应类型
     * @param status 状态描述字符串
     * @param data 数据实体
     * @return
     */
    public static CommonReturnType create(String status, Object data) {
        CommonReturnType returnType = new CommonReturnType();
        returnType.setStatus(status);
        returnType.setData(data);
        return  returnType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
