package cn.tedu.shiro.session;

import cn.tedu.properties.ShiroProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 自定义RedisSessionDao，继承AbstractRedisSessionDao，达到只查一层缓存
 */
@Slf4j
public class RedisSessionDao extends AbstractRedisSessionDao {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ShiroProperties shiroProperties;

    /**
     * 更新session
     * @param session
     */
    @Override
    protected void doUpdate(Session session) {
        log.info("执行redisdao的doUpdate方法");
        redisTemplate.opsForHash().put(shiroProperties.getCache().getSessionHashKey(), session.getId(), session);
    }
    /**
     * 删除session
     * @param session
     */
    @Override
    protected void doDelete(Session session) {
        log.info("执行redisdao的doDelete方法");
        redisTemplate.opsForHash().delete(shiroProperties.getCache().getSessionHashKey(), session.getId());
    }
    /**
     * 创建一个Session，添加到缓存中
     * @param session Session信息
     * @return 创建的SessionId
     */
    @Override
    protected Serializable doCreate(Session session) {
        log.info("执行redisdao的doCreate方法");
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        redisTemplate.opsForHash().put(shiroProperties.getCache().getSessionHashKey(), session.getId(),session);
        return sessionId;
    }
    @Override
    protected Session doReadSession(Serializable sessionId) {
        log.info("执行redisdao的doReadSession方法");
        return (Session) redisTemplate.opsForHash().get(shiroProperties.getCache().getSessionHashKey(),sessionId);
    }
    /**
     * 获取所有的Session
     */
    @Override
    public Collection<Session> getActiveSessions() {
        List values = redisTemplate.opsForHash().values(shiroProperties.getCache().getSessionHashKey());
        if (CollectionUtils.isNotEmpty(values)){
            return values;
        }
        return Collections.emptySet();
    }
}