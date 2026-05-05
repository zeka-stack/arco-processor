---
published: 2022.02.14
---

# 处理器实现

## 📖 作用

`arco-processor-core` 是 Arco Processor 的**核心处理器实现模块**，包含了所有注解处理器的具体实现逻辑。它在编译时扫描项目中的注解，自动生成
Spring Boot 配置文件和 Java SPI 配置文件。

## 🎯 为什么这么设计

### 1. 编译时处理

注解处理器在编译时运行，具有以下优势：

- **零运行时开销**：配置文件在编译时生成，运行时不需要处理器
- **类型安全**：编译时验证，确保配置的正确性
- **开发体验**：IDE 可以实时显示生成的配置文件

### 2. 多处理器架构

框架采用多处理器架构，每个处理器负责不同的功能：

- **AutoFactoriesProcessor**：处理 Spring Boot 自动配置注解
- **AutoServiceProcessor**：处理 Java SPI 注解
- **AotFactoriesProcessor**：处理 AOT 编译注解

### 3. 增量编译支持

框架支持增量编译，提高编译效率：

- **智能合并**：自动合并用户配置和生成的配置
- **变更检测**：只处理变更的文件，减少重复处理
- **缓存机制**：利用编译器的增量编译能力

## 🚀 如何使用

### 1. 配置注解处理器

在 `pom.xml` 中配置：

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

### 2. 自动处理

编译时，处理器会自动：

- 扫描所有使用注解的类
- 生成对应的配置文件
- 验证配置的正确性

### 3. 查看生成的文件

编译完成后，查看生成的文件：

```bash
# Spring Boot 配置文件
cat target/classes/META-INF/spring.factories
cat target/classes/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports

# SPI 配置文件
cat target/classes/META-INF/services/com.example.PaymentService
```

## 📦 处理器说明

### AutoFactoriesProcessor

**功能**：处理 Spring Boot 自动配置相关注解

**生成的配置文件**：

- `META-INF/spring.factories`（Spring Boot 2.x）
- `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`（Spring Boot 3.x）

**支持的注解**：

- `@AutoConfiguration`
- `@AutoContextInitializer`
- `@AutoListener`
- `@AutoRunListener`
- `@AutoEnvPostProcessor`
- `@AutoFailureAnalyzer`
- 等等...

### AutoServiceProcessor

**功能**：处理 Java SPI 服务提供者注解

**生成的配置文件**：

- `META-INF/services/[接口全限定名]`

**支持的注解**：

- `@AutoService`

### AotFactoriesProcessor

**功能**：处理 Spring Boot 3.x AOT 编译注解

**生成的配置文件**：

- `META-INF/spring/aot.factories`

**支持的注解**：

- `@AotRuntimeHintsRegistrar`
- `@AotBeanRegistration`
- `@AotBeanFactoryInitialization`

## 🔧 实现细节

### 1. 注解扫描

处理器使用 Java 编译器的 API 扫描注解：

```java
@Override
public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    // 扫描所有使用 @AutoConfiguration 的类
    Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(AutoConfiguration.class);
    // 处理每个元素
    for (Element element : elements) {
        // 生成配置
    }
}
```

### 2. 配置文件生成

处理器使用 `Filer` API 生成文件：

```java
Filer filer = processingEnv.getFiler();
FileObject fileObject = filer.createResource(
    StandardLocation.CLASS_OUTPUT,
    "",
    "META-INF/spring.factories"
);
// 写入配置内容
```

### 3. 增量编译支持

处理器实现了 `IncrementalAnnotationProcessor` 接口：

```java
@Override
public Set<String> getSupportedOptions() {
    return Set.of(INCREMENTAL_PROCESSING_OPTION);
}
```

## 📝 最佳实践

1. **正确配置**：确保注解处理器路径配置正确
2. **版本兼容**：注意 Spring Boot 版本兼容性
3. **调试模式**：使用 `-Adebug=true` 启用调试日志

## 🔗 相关链接

- [[arco-meta/arco-processor/index|处理器总览]]
- [[arco-meta/arco-processor/arco-processor-annotation|注解定义]]
- [[arco-meta/arco-processor/arco-processor-sample/index|使用示例]]
