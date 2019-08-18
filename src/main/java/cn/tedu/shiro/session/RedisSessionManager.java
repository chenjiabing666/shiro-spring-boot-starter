package cn.tedu.shiro.session;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;

/**
 * 自定义的会话管理器
 */
@Slf4j
public class RedisSessionManager extends DefaultWebSessionManager {
    /**
     * 前后端分离不存在cookie，因此需要重写getSessionId的逻辑，从请求参数中获取
     * 此处的逻辑：在登录成功之后会将sessionId作为一个token返回，下次请求的时候直接带着token即可
     */
    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        //获取上传的token,这里的token就是sessionId
        return request.getParameter("token");
    }
    /**
     * 重写该方法，在SessionManager中只要涉及到Session的操作都会获取Session，获取Session主要是从缓存中获取，父类的该方法执行逻辑如下：
     *  1、先从RedisCache中获取，调用get方法
     *  2、如果RedisCache中不存在，在从SessionDao中获取，调用get方法
     *  优化：我们只需要从SessionDao中获取即可
     * @param sessionKey Session的Key
     */
    @Override
    protected Session retrieveSession(SessionKey sessionKey) throws UnknownSessionException {
        //获取SessionId
        Serializable sessionId = getSessionId(sessionKey);
        if (sessionId == null) {
            log.debug("Unable to resolve session ID from SessionKey [{}].  Returning null to indicate a " +
                    "session could not be found.", sessionKey);
            return null;
        }
        //直接调用SessionDao中的get方法获取
        Session session = ((RedisSessionDao) sessionDAO).doReadSession(sessionId);
        if (session == null) {
            //session ID was provided, meaning one is expected to be found, but we couldn't find one:
            String msg = "Could not find session with ID [" + sessionId + "]";
            throw new UnknownSessionException(msg);
        }
        return session;
    }
}