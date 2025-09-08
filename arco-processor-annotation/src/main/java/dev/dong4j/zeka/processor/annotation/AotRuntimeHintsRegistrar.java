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
 * AOT 运行时提示注册器注解
 *
 * <p>该注解用于标记实现了 {@code org.springframework.aot.hint.RuntimeHintsRegistrar} 接口的类，
 * 注解处理器会自动将被标记的类注册到 {@code META-INF/spring/aot.factories} 文件中，
 * 使其在 Spring Boot 3.x AOT（Ahead-of-Time）编译过程中被自动发现和执行。</p>
 *
 * <p><strong>使用场景：</strong></p>
 * <ul>
 *   <li>为 GraalVM 原生镜像提供反射、代理、资源等运行时提示</li>
 *   <li>确保原生编译时包含必要的运行时信息</li>
 *   <li>自定义原生镜像的构建配置</li>
 *   <li>解决原生编译时的类访问问题</li>
 * </ul>
 *
 * <p><strong>注册目标：</strong><br>
 * {@code org.springframework.aot.hint.RuntimeHintsRegistrar}</p>
 *
 * <p><strong>示例用法：</strong></p>
 * <pre>{@code
 * @AotRuntimeHintsRegistrar
 * public class CustomRuntimeHintsRegistrar implements RuntimeHintsRegistrar {
 *     @Override
 *     public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
 *         // 注册反射提示
 *         hints.reflection().registerType(MyClass.class, MemberCategory.INVOKE_PUBLIC_METHODS);
 *
 *         // 注册资源提示
 *         hints.resources().registerPattern("config/application-*.yml");
 *
 *         // 注册代理提示
 *         hints.proxies().registerJdkProxy(MyInterface.class);
 *     }
 * }
 * }</pre>
 *
 * @author L.cm
 * @see org.springframework.aot.hint.RuntimeHintsRegistrar
 * @since 1.0.0
 */
@Documented
@Retention(SOURCE)
@Target(TYPE)
public @interface AotRuntimeHintsRegistrar {

}
