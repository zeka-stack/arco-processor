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

package dev.dong4j.zeka.processor.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * 自动启用断路器注解
 *
 * <p>该注解用于标记实现了断路器功能的类，注解处理器会自动将被标记的类注册到
 * {@code META-INF/spring.factories} 文件中，使其在 Spring Boot 应用启动时被自动发现和启用。</p>
 *
 * <p><strong>使用场景：</strong></p>
 * <ul>
 *   <li>自动启用断路器保护机制</li>
 *   <li>集成 Hystrix、Resilience4j 等断路器库</li>
 *   <li>提供服务容错和熔断保护</li>
 *   <li>微服务架构中的服务降级处理</li>
 * </ul>
 *
 * <p><strong>注册目标：</strong><br>
 * {@code org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker}</p>
 *
 * <p><strong>示例用法：</strong></p>
 * <pre>{@code
 * @AutoEnableCircuitBreaker
 * @Configuration
 * public class CircuitBreakerAutoConfiguration {
 *
 *     @Bean
 *     public CircuitBreakerFactory circuitBreakerFactory() {
 *         return new CustomCircuitBreakerFactory();
 *     }
 *
 *     @Bean
 *     public CircuitBreakerConfigurer circuitBreakerConfigurer() {
 *         return new DefaultCircuitBreakerConfigurer();
 *     }
 * }
 * }</pre>
 *
 * @author L.cm
 * @see org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker
 * @since 1.0.0
 */
@Documented
@Retention(SOURCE)
@Target(TYPE)
public @interface AutoEnableCircuitBreaker {

}
