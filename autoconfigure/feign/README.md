# Feign Auto-Configuration

This utility adds autoconfiguration for Feign clients using [Spring Cloud OpenFeign](https://spring.io/projects/spring-cloud-openfeign)

To utilize in your project:
```groovy
dependencies {
    implementation 'com.srivastavavivekggn:autoconfigure-feign:0.75.0'
}
```

And in your property configuration, you can specify
```yaml
feign:
  base-package: com.my.package
```

This will translate to the following:
```java
@EnableFeignClients(basePackages = "com.my.package")
```


This project exports a dependency on
```groovy
org.springframework.cloud:spring-cloud-starter-openfeign
```
and all of it's transitive dependencies