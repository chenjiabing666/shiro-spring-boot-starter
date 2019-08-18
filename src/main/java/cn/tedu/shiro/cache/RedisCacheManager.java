package cn.tedu.shiro.cache;

import cn.tedu.properties.ShiroProperties;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Redis的CacheManager
 */
public class RedisCacheManager implements CacheManager {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ShiroProperties shiroProperties;

    @Override
    public <K, V> Cache<K, V> getCache(String s) throws CacheException {
        return new RedisCache<K,V>(redisTemplate, shiroProperties.getCache().getCacheKey()+":"+s);
    }
}