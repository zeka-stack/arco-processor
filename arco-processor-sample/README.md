# Arco Processor 使用示例

本目录包含了 Arco Processor 注解处理器的完整使用示例，演示了所有支持的注解和特性。

## 示例结构

```
arco-processor-sample/
├── single-module/          # 单模块示例项目
│   └── src/main/java/
│       └── sample.processor/
│           ├── SampleApplication.java      # 启动类
│           ├── config/                     # 自动配置示例
│           ├── controller/                 # Web 控制器
│           ├── env/                        # 环境处理器示例
│           ├── failure/                    # 故障分析器示例
│           ├── ignored/                    # 忽略注解示例
│           ├── initializer/                # 上下文初始化器示例
│           ├── listener/                   # 应用监听器示例
│           ├── runlistener/                # 运行监听器示例
│           └── spi/                        # SPI 服务示例
└── multi-module/           # 多模块示例项目
```

## 运行示例

### 1. 编译项目

```bash
cd arco-processor-sample/single-module
mvn clean compile
```

### 2. 查看生成的配置文件

编译完成后，检查生成的配置文件：

```bash
# Spring Boot 自动配置文件
cat target/classes/META-INF/spring.factories
cat target/classes/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports

# SPI 服务配置文件
cat target/classes/META-INF/services/sample.processor.spi.PaymentService
```

### 3. 启动应用

```bash
mvn spring-boot:run
```

### 4. 测试 API 端点

访问以下端点测试各种功能：

```bash
# 测试自动配置服务
curl http://localhost:8080/sample/config?message=Hello

# 测试环境配置
curl http://localhost:8080/sample/env

# 测试 SPI 服务
curl http://localhost:8080/sample/payment?amount=100&currency=CNY

# 测试故障分析器（触发异常）
curl http://localhost:8080/sample/error
```

## 各类注解示例说明

### 自动配置注解 (`@AutoConfiguration`)

**文件**：`config/AutoConfigurationSample.java`

```java
@AutoConfiguration
@ConditionalOnProperty(prefix = "sample.processor", name = "enabled", havingValue = "true")
public class AutoConfigurationSample {
    @Bean
    public SampleService sampleService() {
        return new SampleService();
    }
}
```

**效果**：

- 自动添加到 `spring.factories` 的 `EnableAutoConfiguration` 配置项
- 自动添加到 `AutoConfiguration.imports` 文件

### 应用监听器 (`@AutoListener`)

**文件**：`listener/CustomApplicationListener.java`

```java
@AutoListener
public class CustomApplicationListener implements ApplicationListener<ApplicationReadyEvent> {
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        System.out.println("应用已就绪：" + event.getTimeTaken().toMillis() + "ms");
    }
}
```

**效果**：自动注册到 `ApplicationListener` 配置项

### 应用上下文初始化器 (`@AutoContextInitializer`)

**文件**：`initializer/CustomContextInitializer.java`

```java
@AutoContextInitializer
public class CustomContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        // 添加自定义属性源
    }
}
```

**效果**：自动注册到 `ApplicationContextInitializer` 配置项

### 环境后置处理器 (`@AutoEnvPostProcessor`)

**文件**：`env/CustomEnvironmentPostProcessor.java`

```java
@AutoEnvPostProcessor
public class CustomEnvironmentPostProcessor implements EnvironmentPostProcessor {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // 修改环境配置
    }
}
```

**效果**：自动注册到 `EnvironmentPostProcessor` 配置项

### Spring 应用运行监听器 (`@AutoRunListener`)

**文件**：`runlistener/CustomSpringApplicationRunListener.java`

```java
@AutoRunListener
public class CustomSpringApplicationRunListener implements SpringApplicationRunListener {
    public CustomSpringApplicationRunListener(SpringApplication application, String[] args) {
        // 必须提供此构造函数
    }

    @Override
    public void starting(ConfigurableBootstrapContext bootstrapContext) {
        System.out.println("应用开始启动");
    }
}
```

**效果**：自动注册到 `SpringApplicationRunListener` 配置项

### 故障分析器 (`@AutoFailureAnalyzer`)

**文件**：`failure/CustomFailureAnalyzer.java`

```java
@AutoFailureAnalyzer
public class CustomFailureAnalyzer extends AbstractFailureAnalyzer<IllegalArgumentException> {
    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, IllegalArgumentException cause) {
        return new FailureAnalysis("错误描述", "解决建议", cause);
    }
}
```

**效果**：自动注册到 `FailureAnalyzer` 配置项

### Java SPI 服务 (`@AutoService`)

**文件**：`spi/AlipayService.java`, `spi/WechatPayService.java`

```java
@AutoService(PaymentService.class)
public class AlipayService implements PaymentService {
    @Override
    public PaymentResult processPayment(double amount, String currency) {
        // 支付宝支付实现
    }
}
```

**效果**：自动生成 `META-INF/services/sample.processor.spi.PaymentService` 文件

### 忽略注解 (`@AutoIgnore`)

**文件**：`ignored/IgnoredConfiguration.java`

```java
@AutoIgnore
@AutoConfiguration
public class IgnoredConfiguration {
    // 此类不会被注解处理器处理
}
```

**效果**：即使有 `@AutoConfiguration` 注解，也不会被自动注册

## 启动日志观察

启动应用时，可以观察到各种组件的执行顺序：

```
=== 自定义环境后置处理器被调用 ===
=== 自定义上下文初始化器被调用 ===
=== 自定义运行监听器：应用开始启动 ===
=== 自定义运行监听器：环境准备完成 ===
=== 自定义运行监听器：应用上下文准备完成 ===
=== 自定义运行监听器：应用上下文加载完成 ===
=== 自定义运行监听器：应用启动完成 ===
=== 应用已就绪，自定义监听器被触发 ===
=== 自定义运行监听器：应用就绪 ===
```

## 生成的配置文件示例

### `spring.factories`

```properties
org.springframework.context.ApplicationContextInitializer=\
  sample.processor.initializer.CustomContextInitializer

org.springframework.context.ApplicationListener=\
  sample.processor.listener.CustomApplicationListener

org.springframework.boot.SpringApplicationRunListener=\
  sample.processor.runlistener.CustomSpringApplicationRunListener

org.springframework.boot.env.EnvironmentPostProcessor=\
  sample.processor.env.CustomEnvironmentPostProcessor

org.springframework.boot.diagnostics.FailureAnalyzer=\
  sample.processor.failure.CustomFailureAnalyzer

org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
  sample.processor.config.AutoConfigurationSample
```

### `AutoConfiguration.imports`

```
sample.processor.config.AutoConfigurationSample
```

### `META-INF/services/sample.processor.spi.PaymentService`

```
sample.processor.spi.AlipayService
sample.processor.spi.WechatPayService
```

## 调试技巧

### 1. 启用编译调试

在 `pom.xml` 中添加：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <compilerArgs>
            <arg>-Adebug=true</arg>
        </compilerArgs>
    </configuration>
</plugin>
```

### 2. 查看编译日志

```bash
mvn clean compile -X
```

### 3. 验证配置生效

通过 Spring Boot Actuator 查看自动配置报告：

```bash
curl http://localhost:8080/actuator/conditions
```

## 常见问题

### 1. 配置文件未生成

**原因**：注解处理器未正确配置或类路径问题

**解决**：

- 检查 `maven-compiler-plugin` 配置
- 确保 `arco-processor-core` 在 `annotationProcessorPaths` 中
- 查看编译错误日志

### 2. SPI 服务未发现

**原因**：服务实现类缺少无参构造函数或未实现接口

**解决**：

- 确保实现类有 `public` 无参构造函数
- 检查是否正确实现了服务接口
- 验证生成的配置文件内容

### 3. 自动配置未生效

**原因**：条件注解不满足或包扫描路径问题

**解决**：

- 检查 `@ConditionalOn*` 注解条件
- 确保配置类在 Spring Boot 扫描路径内
- 查看自动配置报告

## 扩展示例

基于此示例，你可以：

1. 添加更多的自定义配置类
2. 实现其他 Spring Boot 扩展点
3. 创建自己的 Starter 项目
4. 集成到现有项目中

参考 `multi-module` 示例了解多模块项目的使用方式。
