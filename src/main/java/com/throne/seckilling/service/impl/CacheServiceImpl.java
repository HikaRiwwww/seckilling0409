package com.throne.seckilling.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.throne.seckilling.service.CacheService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * com.throne.seckilling.service.impl
 * Created by throne on 2020/5/7
 */
@Service
public class CacheServiceImpl implements CacheService {

    private Cache<String, Object> commonCache = null;

    @PostConstruct
    public void init(){
        commonCache = CacheBuilder.newBuilder()
                // 初始容量
                .initialCapacity(10)
                // 最大容量，超过后LRU删除
                .maximumSize(100)
                // 过期时间
                .expireAfterWrite(60, TimeUnit.SECONDS).build();
    }

    @Override
    public void setCommonCache(String key, Object value) {
        commonCache.put(key, value);
    }

    @Override
    public Object getCommonCache(String key) {
        return commonCache.getIfPresent(key);
    }
}
