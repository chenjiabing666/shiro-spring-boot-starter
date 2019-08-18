package cn.tedu.properties;

import lombok.Data;

@Data
public class CacheProperties {
    private String cacheKey;
    private String sessionHashKey;
    private Long sessionExpireTime;
    public CacheProperties(){
        this.cacheKey="SHIRO_DEMO";
        this.sessionHashKey="SHIRO_USER";
        this.sessionExpireTime=600000L;
    }
}
