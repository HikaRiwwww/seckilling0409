package com.throne.seckilling.service;

/**
 * 封装本地缓存的类
 * com.throne.seckilling.service
 * Created by throne on 2020/5/7
 */
public interface CacheService {
    void setCommonCache(String key, Object value);

    Object getCommonCache(String key);
}
