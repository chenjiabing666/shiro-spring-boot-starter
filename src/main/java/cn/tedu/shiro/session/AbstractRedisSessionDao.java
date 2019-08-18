package cn.tedu.shiro.session;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;

import java.io.Serializable;

/**
 * RedisSessionDao的抽象类，重写其中的增删改查方法，原因如下：
 *  1、AbstractSessionDAO中的默认方法是写查询CacheManager中的缓存，既然SessionDao实现了Redis的缓存
 *      那么就不需要重复查询两次，因此重写了方法，直接使用RedisSessionDao查询即可。
 */
public abstract class AbstractRedisSessionDao extends AbstractSessionDAO {
    /**
     * 重写creat方法，直接执行sessionDao的方法，不再执行cacheManager
     * @param session
     * @return
     */
    @Override
    public Serializable create(Session session) {
        Serializable sessionId = doCreate(session);
        if (sessionId == null) {
            String msg = "sessionId returned from doCreate implementation is null.  Please verify the implementation.";
            throw new IllegalStateException(msg);
        }
        return sessionId;
    }
    /**
     * 重写删除操作
     * @param session
     */
    @Override
    public void delete(Session session) {
        doDelete(session);
    }
    /**
     * 重写update方法
     * @param session
     * @throws UnknownSessionException
     */
    @Override
    public void update(Session session) throws UnknownSessionException {
        doUpdate(session);
    }
    /**
     * 重写查找方法
     * @param sessionId
     * @return
     * @throws UnknownSessionException
     */
    @Override
    public Session readSession(Serializable sessionId) throws UnknownSessionException {
        Session s = doReadSession(sessionId);
        if (s == null) {
            throw new UnknownSessionException("There is no session with id [" + sessionId + "]");
        }
        return s;
    }
    protected abstract void doDelete(Session session);

    protected abstract void doUpdate(Session session);
}
