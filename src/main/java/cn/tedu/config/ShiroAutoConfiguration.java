package cn.tedu.config;

import cn.tedu.properties.ShiroProperties;
import cn.tedu.shiro.cache.RedisCacheManager;
import cn.tedu.shiro.realm.AutoConfigRealm;
import cn.tedu.shiro.session.RedisSessionDao;
import cn.tedu.shiro.session.RedisSessionManager;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.Filter;

/**
 * Shiro的自动配置类
 * @AutoConfigureAfter：Shiro运行需要web环境和Redis环境（做了缓存），因此要在他们自动配置完成之后再执行配置
 * @ConditionalOnProperty：默认开始Shiro的功能，但是可以在自定义配置关闭
 * @EnableConfigurationProperties：引入Shiro的配置
 */
@Configuration
@AutoConfigureAfter({WebMvcAutoConfiguration.class, RedisAutoConfiguration.class})
@ConditionalOnProperty(name = "enabled",prefix = "shiro",havingValue = "true")
@EnableConfigurationProperties(value = {ShiroProperties.class})
public class ShiroAutoConfiguration {

    @Autowired
    private ShiroProperties shiroProperties;

    /**
     * 注入LifecycleBeanPostProcessor
     */
    @Bean
    public static LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * AuthorizationAttributeSourceAdvisor：注入一个Advisor，用来拦截
     *  RequiresPermissions.class, RequiresRoles.class,
     *  RequiresUser.class, RequiresGuest.class, RequiresAuthentication.class
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(){
        return new AuthorizationAttributeSourceAdvisor();
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    /**
     * 添加过滤器
     */
    @Bean
    @ConditionalOnMissingBean
    public FilterRegistrationBean filterRegistrationBean(){
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        DelegatingFilterProxy filterProxy = new DelegatingFilterProxy();
        registrationBean.setFilter(filterProxy);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("shiroFilter");
        registrationBean.addInitParameter("targetFilterLifecycle", "true");
        registrationBean.addInitParameter("targetBeanName", "shiroFilter");
        return registrationBean;
    }


    /**
     * 注入realm，可以覆盖
     */
    @Bean
    @ConditionalOnMissingBean
    public Realm realm(CredentialsMatcher credentialsMatcher){
        AutoConfigRealm realm = new AutoConfigRealm();
        realm.setCredentialsMatcher(credentialsMatcher);
        return realm;
    }

    /**
     * 注册凭证匹配器
     */
    @Bean
    @ConditionalOnMissingBean
    public CredentialsMatcher credentialsMatcher(){
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher(shiroProperties.getCredentialsMatcher().getHashAlgorithmName());
        credentialsMatcher.setHashIterations(shiroProperties.getCredentialsMatcher().getHashIterations());
        return credentialsMatcher;
    }

    /**
     * 配置缓存管理器，可以用于认证和授权信息的缓存
     */
    @Bean
    @ConditionalOnMissingBean
    public CacheManager cacheManager(){
        return new RedisCacheManager();
    }


    /**
     * 注入SessionDao，实现session的缓存
     */
    @Bean
    @ConditionalOnMissingBean
    public SessionDAO sessionDAO(){
        return new RedisSessionDao();
    }

    /**
     * 注入的Session管理器
     */
    @Bean
    @ConditionalOnMissingBean
    public SessionManager sessionManager(SessionDAO sessionDAO){
        RedisSessionManager sessionManager = new RedisSessionManager();
        //设置session的过期时间
        sessionManager.setGlobalSessionTimeout(shiroProperties.getCache().getSessionExpireTime());
        //设置SessionDao
        sessionManager.setSessionDAO(sessionDAO);
        return sessionManager;
    }

    /**
     * 配置安全管理器
     */
    @Bean
    @ConditionalOnMissingBean
    public SecurityManager securityManager(Realm realm,CacheManager cacheManager,SessionManager sessionManager){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager(realm);
        //设置缓存管理器
        securityManager.setCacheManager(cacheManager);
        //设置会话管理器
        securityManager.setSessionManager(sessionManager);
        return securityManager;
    }

    /**
     * 注入ShiroFilterFactoryBean
     */
    @Bean
    @ConditionalOnMissingBean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager SecurityManager){
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        //设置安全管理器
        shiroFilter.setSecurityManager(SecurityManager);
        //配置登录的uri,如果没有的登录，跳转的uri
        if (StringUtils.hasLength(shiroProperties.getLoginUrl())){
            shiroFilter.setLoginUrl(shiroProperties.getLoginUrl());
        }
        if (StringUtils.hasLength(shiroProperties.getSuccessUrl())){
            shiroFilter.setSuccessUrl(shiroProperties.getSuccessUrl());
        }
        if (StringUtils.hasLength(shiroProperties.getUnauthorizedUrl())){
            //没有权限跳转的url
            shiroFilter.setUnauthorizedUrl(shiroProperties.getUnauthorizedUrl());
        }
        //配置url的权限
        shiroFilter.setFilterChainDefinitionMap(shiroProperties.getFilterChainDefinitionMap());
        return shiroFilter;
    }
}
