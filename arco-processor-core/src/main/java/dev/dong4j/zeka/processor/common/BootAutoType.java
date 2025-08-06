/*
 * Copyright (c) 2019-2029, Dreamlu 卢春梦 (596392912@qq.com & www.dreamlu.net).
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.dong4j.zeka.processor.common;

import dev.dong4j.zeka.processor.annotation.AutoConfigDataLoader;
import dev.dong4j.zeka.processor.annotation.AutoConfigDataLocationResolver;
import dev.dong4j.zeka.processor.annotation.AutoConfigImportFilter;
import dev.dong4j.zeka.processor.annotation.AutoContextInitializer;
import dev.dong4j.zeka.processor.annotation.AutoDatabaseInitializerDetector;
import dev.dong4j.zeka.processor.annotation.AutoDependsOnDatabaseInitializationDetector;
import dev.dong4j.zeka.processor.annotation.AutoEnableCircuitBreaker;
import dev.dong4j.zeka.processor.annotation.AutoEnvPostProcessor;
import dev.dong4j.zeka.processor.annotation.AutoFailureAnalyzer;
import dev.dong4j.zeka.processor.annotation.AutoListener;
import dev.dong4j.zeka.processor.annotation.AutoLoggingSystemFactory;
import dev.dong4j.zeka.processor.annotation.AutoRunListener;
import dev.dong4j.zeka.processor.annotation.AutoTemplateProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Spring Boot 自动配置注解类型枚举
 *
 * <p>该枚举定义了所有支持的 Spring Boot 自动配置注解类型及其对应的配置键。
 * 每个枚举值包含注解的全限定名和在 {@code spring.factories} 文件中的配置键。</p>
 *
 * <p><strong>配置文件格式：</strong></p>
 * <pre>
 * # spring.factories 文件格式
 * [configureKey]=[实现类1],[实现类2],...
 * </pre>
 *
 * <p><strong>支持的扩展点：</strong></p>
 * <ul>
 *   <li>自动配置类：{@code @Component} 及其派生注解</li>
 *   <li>应用上下文初始化器：{@link AutoContextInitializer}</li>
 *   <li>应用监听器：{@link AutoListener}</li>
 *   <li>运行监听器：{@link AutoRunListener}</li>
 *   <li>环境后置处理器：{@link AutoEnvPostProcessor}</li>
 *   <li>故障分析器：{@link AutoFailureAnalyzer}</li>
 *   <li>配置导入过滤器：{@link AutoConfigImportFilter}</li>
 *   <li>模板可用性提供者：{@link AutoTemplateProvider}</li>
 *   <li>断路器启用：{@link AutoEnableCircuitBreaker}</li>
 *   <li>配置数据定位解析器：{@link AutoConfigDataLocationResolver}</li>
 *   <li>配置数据加载器：{@link AutoConfigDataLoader}</li>
 *   <li>数据库初始化检测器：{@link AutoDatabaseInitializerDetector}</li>
 *   <li>数据库初始化依赖检测器：{@link AutoDependsOnDatabaseInitializationDetector}</li>
 *   <li>日志系统工厂：{@link AutoLoggingSystemFactory}</li>
 * </ul>
 *
 * @author L.cm
 * @see dev.dong4j.zeka.processor.factories.AutoFactoriesProcessor
 * @since 2.0.0
 */
@Getter
@RequiredArgsConstructor
public enum BootAutoType {

    /** Component，组合注解，添加到 spring.factories */
    COMPONENT(BootAutoType.COMPONENT_ANNOTATION, "org.springframework.boot.autoconfigure.EnableAutoConfiguration"),
    /** ApplicationContextInitializer 添加到 spring.factories */
    CONTEXT_INITIALIZER(AutoContextInitializer.class.getName(), "org.springframework.context.ApplicationContextInitializer"),
    /** ApplicationListener 添加到 spring.factories */
    LISTENER(AutoListener.class.getName(), "org.springframework.context.ApplicationListener"),
    /** SpringApplicationRunListener 添加到 spring.factories */
    RUN_LISTENER(AutoRunListener.class.getName(), "org.springframework.boot.SpringApplicationRunListener"),
    /** EnvironmentPostProcessor 添加到 spring.factories */
    ENV_POST_PROCESSOR(AutoEnvPostProcessor.class.getName(), "org.springframework.boot.env.EnvironmentPostProcessor"),
    /** FailureAnalyzer 添加到 spring.factories */
    FAILURE_ANALYZER(AutoFailureAnalyzer.class.getName(), "org.springframework.boot.diagnostics.FailureAnalyzer"),
    /** AutoConfigurationImportFilter spring.factories */
    AUTO_CONFIGURATION_IMPORT_FILTER(AutoConfigImportFilter.class.getName(), "org.springframework.boot.autoconfigure.AutoConfigurationImportFilter"),
    /** TemplateAvailabilityProvider 添加到 spring.factories */
    TEMPLATE_AVAILABILITY_PROVIDER(AutoTemplateProvider.class.getName(), "org.springframework.boot.autoconfigure.template.TemplateAvailabilityProvider"),
    /** auto EnableCircuitBreaker */
    AUTO_ENABLE_CIRCUIT_BREAKER(AutoEnableCircuitBreaker.class.getName(), "org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker"),
    /** auto ConfigDataLocationResolver */
    AUTO_CONFIG_DATA_LOCATION_RESOLVER(AutoConfigDataLocationResolver.class.getName(), "org.springframework.boot.context.config.ConfigDataLocationResolver"),
    /** auto ConfigDataLoader */
    AUTO_CONFIG_DATA_LOADER(AutoConfigDataLoader.class.getName(), "org.springframework.boot.context.config.ConfigDataLoader"),
    /** auto DatabaseInitializerDetector */
    AUTO_DATABASE_INITIALIZER_DETECTOR(AutoDatabaseInitializerDetector.class.getName(), "org.springframework.boot.sql.init.dependency.DatabaseInitializerDetector"),
    /** auto DependsOnDatabaseInitializationDetector */
    AUTO_DEPENDS_ON_DATABASE_INITIALIZATION_DETECTOR(AutoDependsOnDatabaseInitializationDetector.class.getName(), "org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitializationDetector"),
    /** auto LoggingSystemFactory */
    AUTO_LOGGING_SYSTEM(AutoLoggingSystemFactory.class.getName(), "org.springframework.boot.logging.LoggingSystemFactory"),
    ;

    private final String annotation;
    private final String configureKey;

    public static final String COMPONENT_ANNOTATION = "org.springframework.stereotype.Component";

}
