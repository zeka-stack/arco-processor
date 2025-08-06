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
 * 自动模板可用性提供者注解
 *
 * <p>该注解用于标记实现了 {@code org.springframework.boot.autoconfigure.template.TemplateAvailabilityProvider} 接口的类，
 * 注解处理器会自动将被标记的类注册到 {@code META-INF/spring.factories} 文件中，
 * 使其在 Spring Boot 应用中被自动发现，用于检测模板引擎的可用性。</p>
 *
 * <p><strong>使用场景：</strong></p>
 * <ul>
 *   <li>集成自定义模板引擎</li>
 *   <li>检测模板文件的存在性</li>
 *   <li>动态选择模板渲染方案</li>
 *   <li>支持多种模板格式</li>
 * </ul>
 *
 * <p><strong>注册目标：</strong><br>
 * {@code org.springframework.boot.autoconfigure.template.TemplateAvailabilityProvider}</p>
 *
 * <p><strong>示例用法：</strong></p>
 * <pre>{@code
 * @AutoTemplateProvider
 * public class CustomTemplateAvailabilityProvider implements TemplateAvailabilityProvider {
 *     @Override
 *     public boolean isTemplateAvailable(String view, Environment environment,
 *                                       ClassLoader classLoader, ResourceLoader resourceLoader) {
 *         // 检测模板是否可用的逻辑
 *         return true;
 *     }
 * }
 * }</pre>
 *
 * @author L.cm
 * @see org.springframework.boot.autoconfigure.template.TemplateAvailabilityProvider
 * @since 2.0.0
 */
@Documented
@Retention(SOURCE)
@Target(TYPE)
public @interface AutoTemplateProvider {
}
