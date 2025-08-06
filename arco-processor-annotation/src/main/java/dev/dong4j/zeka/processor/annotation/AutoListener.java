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
 * 自动应用监听器注解
 *
 * <p>该注解用于标记实现了 {@code org.springframework.context.ApplicationListener} 接口的类，
 * 注解处理器会自动将被标记的类注册到 {@code META-INF/spring.factories} 文件中，
 * 使其在 Spring 应用上下文中被自动发现和注册为事件监听器。</p>
 *
 * <p><strong>使用场景：</strong></p>
 * <ul>
 *   <li>监听应用启动、停止等生命周期事件</li>
 *   <li>监听自定义应用事件</li>
 *   <li>实现跨模块的事件通信</li>
 *   <li>系统状态监控和日志记录</li>
 * </ul>
 *
 * <p><strong>注册目标：</strong><br>
 * {@code org.springframework.context.ApplicationListener}</p>
 *
 * <p><strong>示例用法：</strong></p>
 * <pre>{@code
 * @AutoListener
 * public class CustomApplicationListener implements ApplicationListener<ApplicationReadyEvent> {
 *     @Override
 *     public void onApplicationEvent(ApplicationReadyEvent event) {
 *         // 处理应用就绪事件
 *     }
 * }
 * }</pre>
 *
 * @author L.cm
 * @since 2.0.0
 * @see org.springframework.context.ApplicationListener
 */
@Documented
@Retention(SOURCE)
@Target(TYPE)
public @interface AutoListener {
}
