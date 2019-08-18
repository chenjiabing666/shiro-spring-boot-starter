package cn.tedu.properties;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class RealmProperties {
    /**
     * 默认登录的用户名
     */
    private String userName="zhangsan";
    /**
     * 密码，默认123456
     */
    private String password="9bad41710724cf6511abde2a52416881";

    /**
     * 盐
     */
    private String salt="zhangsan";

    /**
     * 角色
     */
    private Set<String> roles=new HashSet<>();
    /**
     * 权限
     */
    private Set<String> permissions=new HashSet<>();
}
