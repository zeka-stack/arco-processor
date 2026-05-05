---
published: 2022.02.07
---

# 注解定义模块

## 📖 作用

`arco-processor-annotation` 是 Arco Processor 的**注解定义模块**，提供了所有用于编译时处理的注解定义。这些注解用于标记需要自动生成 Spring Boot
配置文件和 Java SPI 配置文件的类。

## 🎯 为什么这么设计

### 1. 注解与处理器分离

将注解定义和处理器实现分离是 Java 注解处理器的标准做法：

- **依赖最小化**：使用注解的项目只需要引入注解定义，不需要引入处理器实现
- **编译时处理**：注解处理器只在编译时运行，运行时不需要
- **清晰职责**：注解定义和处理器实现职责分离，便于维护

### 2. 注解分类

框架提供了多类注解，覆盖 Spring Boot 的各种扩展点：

#### Spring Boot 自动配置注解

- `@AutoConfiguration` - 自动配置类
- `@AutoContextInitializer` - 应用上下文初始化器
- `@AutoListener` - 应用监听器
- `@AutoRunListener` - Spring 应用运行监听器
- `@AutoEnvPostProcessor` - 环境后置处理器
- `@AutoFailureAnalyzer` - 故障分析器
- 等等...

#### Java SPI 注解

- `@AutoService` - Java SPI 服务提供者

#### AOT 编译注解

- `@AotRuntimeHintsRegistrar` - 运行时提示注册器
- `@AotBeanRegistration` - Bean 注册 AOT 处理器
- `@AotBeanFactoryInitialization` - Bean 工厂初始化 AOT 处理器

### 3. 控制注解

- `@AutoIgnore` - 忽略指定类，不进行自动处理

## 🚀 如何使用

### 1. 添加依赖

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>arco-processor-annotation</artifactId>
    <version>2.0.0-SNAPSHOT</version>
</dependency>
```

### 2. 使用注解

```java
// 自动配置类
@AutoConfiguration
public class MyAutoConfiguration {
    @Bean
    public MyService myService() {
        return new MyService();
    }
}

// SPI 服务
@AutoService(PaymentService.class)
public class AlipayService implements PaymentService {
    // ...
}
```

### 3. 配置注解处理器

在 `pom.xml` 中配置注解处理器：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>dev.dong4j</groupId>
                <artifactId>arco-processor-core</artifactId>
                <version>2.0.0-SNAPSHOT</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

### 4. 编译项目

```bash
mvn clean compile
```

编译完成后，会自动生成配置文件到 `target/classes/META-INF/` 目录。

## 📦 注解说明

### Spring Boot 自动配置注解

所有 Spring Boot 自动配置相关的注解都会自动生成到：

- `META-INF/spring.factories`（Spring Boot 2.x）
- `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`（Spring Boot 3.x）

### Java SPI 注解

`@AutoService` 注解会自动生成到：

- `META-INF/services/[接口全限定名]`

### AOT 编译注解

AOT 相关注解会生成到：

- `META-INF/spring/aot.factories`

## 🔧 注解特性

### 1. 组合注解支持

支持 Spring 的组合注解特性：

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@AutoConfiguration
@ConditionalOnWebApplication
public @interface WebAutoConfiguration {
}

@WebAutoConfiguration  // 会被识别为 @AutoConfiguration
public class MyWebAutoConfiguration {
    // ...
}
```

### 2. 增量编译支持

注解处理器支持增量编译，会自动合并：

- 用户手动编写的配置文件
- 上次编译生成的配置文件
- 当前编译新发现的配置

### 3. 配置验证

处理器会在编译时验证：

- 服务提供者是否实现了声明的接口
- 配置类是否存在
- 注解使用是否正确

## 📝 最佳实践

1. **正确使用注解**：根据实际需求选择合适的注解
2. **避免重复配置**：不要手动编写配置文件，让注解处理器自动生成
3. **版本兼容**：注意 Spring Boot 2.x 和 3.x 的兼容性

## 🔗 相关链接

- [[arco-meta/arco-processor/index|处理器总览]]
- [[arco-meta/arco-processor/arco-processor-core|处理器实现]]
- [[arco-meta/arco-processor/arco-processor-sample/index|使用示例]]
