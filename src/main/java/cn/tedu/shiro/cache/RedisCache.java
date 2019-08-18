package cn.tedu.shiro.cache;

import cn.tedu.properties.CacheProperties;
import cn.tedu.properties.ShiroProperties;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.Set;

/**
 * RedisCache
 */
public class RedisCache<K,V> implements Cache<K,V> {

    private RedisTemplate redisTemplate;

    /**
     * 存储在redis中的hash中的key
     */
    private String name;

    public RedisCache(RedisTemplate redisTemplate, String name) {
        this.redisTemplate = redisTemplate;
        this.name = name;
    }

    /**
     * 获取指定的key的缓存
     * @param k
     * @return
     * @throws CacheException
     */
    @Override
    public V get(K k) throws CacheException {
        return (V) redisTemplate.opsForHash().get(name,k);
    }
    /**
     * 添加缓存
     * @param k
     * @param v
     * @return
     * @throws CacheException
     */
    @Override
    public V put(K k, V v) throws CacheException {
        redisTemplate.opsForHash().put(name, k, v);
        //设置过期时间
        return v;
    }
    /**
     * 删除指定key的缓存
     * @param k 默认是principle对象，在AuthorizingRealm中设置
     */
    @Override
    public V remove(K k) throws CacheException {
        V v = this.get(k);
        redisTemplate.opsForHash().delete(name, k);
        return v;
    }
    /**
     * 删除所有的缓存
     */
    @Override
    public void clear() throws CacheException {
        redisTemplate.delete(name);
    }
    /**
     * 获取总数
     * @return
     */
    @Override
    public int size() {
        return redisTemplate.opsForHash().size(name).intValue();
    }
    @Override
    public Set<K> keys() {
        return redisTemplate.opsForHash().keys(name);
    }
    @Override
    public Collection<V> values() {
        return redisTemplate.opsForHash().values(name);
    }
}
