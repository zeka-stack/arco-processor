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
 * 自动应用上下文初始化器注解
 *
 * <p>该注解用于标记实现了 {@code org.springframework.context.ApplicationContextInitializer} 接口的类，
 * 注解处理器会自动将被标记的类注册到 {@code META-INF/spring.factories} 文件中，
 * 使其在 Spring 应用上下文初始化时被自动发现和执行。</p>
 *
 * <p><strong>使用场景：</strong></p>
 * <ul>
 *   <li>在应用上下文刷新之前进行自定义初始化</li>
 *   <li>注册自定义的 BeanFactoryPostProcessor</li>
 *   <li>添加自定义的 PropertySource</li>
 *   <li>设置 Profile 激活条件</li>
 * </ul>
 *
 * <p><strong>注册目标：</strong><br>
 * {@code org.springframework.context.ApplicationContextInitializer}</p>
 *
 * <p><strong>示例用法：</strong></p>
 * <pre>{@code
 * @AutoContextInitializer
 * public class CustomContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
 *     @Override
 *     public void initialize(ConfigurableApplicationContext applicationContext) {
 *         // 自定义应用上下文初始化逻辑
 *     }
 * }
 * }</pre>
 *
 * @author L.cm
 * @since 2.0.0
 * @see org.springframework.context.ApplicationContextInitializer
 */
@Documented
@Retention(SOURCE)
@Target(TYPE)
public @interface AutoContextInitializer {
}
