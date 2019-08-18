package cn.tedu.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "shiro")
@Data
public class ShiroProperties {
    /**
     * 是否开启shiro
     */
    private Boolean enabled=true;
    /**
     * Realm的默认配置
     */
    private RealmProperties realm=new RealmProperties();

    /**
     * 凭证匹配器的配置
     */
    private CredentialsMatcherProperties credentialsMatcher=new CredentialsMatcherProperties();


    /**
     * filterChainDefinitionMap的配置
     */
    private Map<String,String> filterChainDefinitionMap=new LinkedHashMap<>();

    /**
     * 缓存的配置
     */
    private CacheProperties cache=new CacheProperties();


    private String loginUrl;

    private String successUrl;

    private String unauthorizedUrl;



}

