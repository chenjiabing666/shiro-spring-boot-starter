# 说明
- 该项目是本人自定义和SpringBoot整合的Shiro的starter（这个只是使用一个自动配置的项目，按照正常流程还需要写一个starter项目引入该依赖即可，此处省略），其中有很多地方只是简单的写了下，如果有不正确的地方还望多多指正
- 自动配置类中的所有的Bean都是可以自定义覆盖的
- 配置示例如下：
```yml
shiro:
  enabled: true
  login-url: /user/unauthentic
  unauthorized-url: /user/unauthorized
  filter-chain-definition-map:
      "[/user/login]": anon
      "[/**]": authc
  realm:
    userName: zhangsan
    salt: zhangsan
    password: 9bad41710724cf6511abde2a52416881
    roles:
      - admin
      - user
  credentials-matcher:
    hashAlgorithmName: MD5
    hashIterations: 1
```
