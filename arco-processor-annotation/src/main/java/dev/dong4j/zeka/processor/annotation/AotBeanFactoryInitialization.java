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
 * AOT Bean 工厂初始化处理器注解
 *
 * <p>该注解用于标记实现了 {@code org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor} 接口的类，
 * 注解处理器会自动将被标记的类注册到 {@code META-INF/spring/aot.factories} 文件中，
 * 使其在 Spring Boot 3.x AOT（Ahead-of-Time）编译过程中被自动发现和执行。</p>
 *
 * <p><strong>使用场景：</strong></p>
 * <ul>
 *   <li>在 AOT 编译时预处理 Bean 工厂初始化逻辑</li>
 *   <li>优化 Bean 工厂的启动性能</li>
 *   <li>生成原生镜像时的工厂处理</li>
 *   <li>自定义 Bean 工厂的 AOT 优化策略</li>
 * </ul>
 *
 * <p><strong>注册目标：</strong><br>
 * {@code org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor}</p>
 *
 * <p><strong>示例用法：</strong></p>
 * <pre>{@code
 * @AotBeanFactoryInitialization
 * public class CustomBeanFactoryInitializationAotProcessor implements BeanFactoryInitializationAotProcessor {
 *     @Override
 *     public BeanFactoryInitializationCodeFragments processAheadOfTime(ConfigurableListableBeanFactory beanFactory) {
 *         // AOT 编译时的 Bean 工厂初始化处理逻辑
 *         return new CustomBeanFactoryInitializationCodeFragments();
 *     }
 * }
 * }</pre>
 *
 * @author L.cm
 * @see org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor
 * @since 1.0.0
 */
@Documented
@Retention(SOURCE)
@Target(TYPE)
public @interface AotBeanFactoryInitialization {
}
