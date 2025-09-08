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
 * 自动忽略注解
 *
 * <p>该注解用于标记那些不希望被注解处理器自动处理的类。当类被此注解标记时，
 * 注解处理器会跳过对该类的所有自动配置处理，即使该类上存在其他自动配置注解。</p>
 *
 * <p><strong>使用场景：</strong></p>
 * <ul>
 *   <li>临时禁用某个类的自动配置功能</li>
 *   <li>防止测试类被误处理</li>
 *   <li>排除不需要自动配置的工具类</li>
 * </ul>
 *
 * <p><strong>示例用法：</strong></p>
 * <pre>{@code
 * @AutoIgnore
 * @Component // 这个注解会被忽略，不会被处理器处理
 * public class IgnoredConfiguration {
 *     // 这个类不会被注册到 spring.factories
 * }
 * }</pre>
 *
 * @author L.cm
 * @since 1.0.0
 */
@Documented
@Retention(SOURCE)
@Target(TYPE)
public @interface AutoIgnore {
}
