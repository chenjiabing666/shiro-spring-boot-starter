package cn.tedu.properties;

import lombok.Data;

/**
 * 凭证匹配器的配置文件
 */
@Data
public class CredentialsMatcherProperties {
    /**
     * 采用的算法
     */
    private String hashAlgorithmName="MD5";
    /**
     * 加密的次数
     */
    private Integer hashIterations=1;
}
