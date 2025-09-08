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

import dev.dong4j.zeka.processor.annotation.AotBeanFactoryInitialization;
import dev.dong4j.zeka.processor.annotation.AotBeanRegistration;
import dev.dong4j.zeka.processor.annotation.AotRuntimeHintsRegistrar;
import dev.dong4j.zeka.processor.aot.AotFactoriesProcessor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Spring Boot AOT 自动配置类型枚举
 *
 * <p>该枚举定义了 Spring Boot 3.x AOT（Ahead-of-Time）编译支持的注解类型及其对应的配置键。
 * AOT 编译可以在编译时预处理 Spring 配置，提高应用启动性能并支持原生镜像编译。</p>
 *
 * <p><strong>AOT 配置文件：</strong></p>
 * <pre>
 * META-INF/spring/aot.factories
 * </pre>
 *
 * <p><strong>支持的 AOT 处理器：</strong></p>
 * <ul>
 *   <li><strong>运行时提示注册器</strong>：为 GraalVM 原生镜像提供反射、代理等运行时提示</li>
 *   <li><strong>Bean 注册 AOT 处理器</strong>：在编译时预处理 Bean 注册逻辑</li>
 *   <li><strong>Bean 工厂初始化 AOT 处理器</strong>：在编译时预处理 Bean 工厂初始化逻辑</li>
 * </ul>
 *
 * <p><strong>AOT 编译优势：</strong></p>
 * <ul>
 *   <li>大幅提高应用启动速度</li>
 *   <li>减少运行时内存占用</li>
 *   <li>支持编译为原生可执行文件</li>
 *   <li>在编译时发现潜在的配置错误</li>
 * </ul>
 *
 * @author L.cm
 * @see AotFactoriesProcessor
 * @see org.springframework.aot.hint.RuntimeHintsRegistrar
 * @see org.springframework.beans.factory.aot.BeanRegistrationAotProcessor
 * @see org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor
 * @since 1.0.0
 */
@Getter
@RequiredArgsConstructor
public enum AotAutoType {

    /** RuntimeHintsRegistrar 添加到 aot.factories */
    RUNTIME_HINTS_REGISTRAR(AotRuntimeHintsRegistrar.class.getName(), "org.springframework.aot.hint.RuntimeHintsRegistrar"),
    /** BeanRegistrationAotProcessor 添加到 aot.factories */
    BEAN_REGISTRATION(AotBeanRegistration.class.getName(), "org.springframework.beans.factory.aot.BeanRegistrationAotProcessor"),
    /** BeanFactoryInitializationAotProcessor 添加到 aot.factories */
    BEAN_FACTORY_INITIALIZATION(AotBeanFactoryInitialization.class.getName(), "org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor");

    private final String annotation;
    private final String configureKey;

}
