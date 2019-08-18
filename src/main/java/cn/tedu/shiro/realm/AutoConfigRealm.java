package cn.tedu.shiro.realm;

import cn.tedu.properties.ShiroProperties;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 默认的认证和授权的Realm
 */
public class AutoConfigRealm extends AuthorizingRealm {
    @Autowired
    private ShiroProperties shiroProperties;

    @Override
    public String getName() {
        return "autoConfigRealm";
    }

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo info=new SimpleAuthorizationInfo();
        info.setRoles(shiroProperties.getRealm().getRoles());
        info.setStringPermissions(shiroProperties.getRealm().getPermissions());
        return info;
    }

    /**
     * 认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        SimpleAuthenticationInfo info=new SimpleAuthenticationInfo(shiroProperties.getRealm().getUserName(), shiroProperties.getRealm().getPassword(), ByteSource.Util.bytes(shiroProperties.getRealm().getSalt()),getName());
        return info;
    }

    /**
     * 清除CacheManager中的缓存，可以在用户权限改变的时候调用，这样再次需要权限的时候就会重新查询数据库不走缓存了
     */
    public void clearCache() {
        Subject subject = SecurityUtils.getSubject();
        //调用父类的清除缓存的方法
        super.clearCache(subject.getPrincipals());
    }
}
