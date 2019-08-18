package cn.tedu.shiro.utils;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

/**
 * Shiro的工具类
 */
public class ShiroUtils {
    /**
     * 登录
     * @param token
     * @return
     */
    public static String login(UsernamePasswordToken token){
        Subject subject = SecurityUtils.getSubject();
        subject.login(token);
        return subject.getSession().getId().toString();
    }


}
