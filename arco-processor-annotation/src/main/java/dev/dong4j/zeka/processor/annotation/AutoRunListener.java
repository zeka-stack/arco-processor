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
 * 自动 Spring 应用运行监听器注解
 *
 * <p>该注解用于标记实现了 {@code org.springframework.boot.SpringApplicationRunListener} 接口的类，
 * 注解处理器会自动将被标记的类注册到 {@code META-INF/spring.factories} 文件中，
 * 使其在 Spring Boot 应用运行过程中被自动发现和执行。</p>
 *
 * <p><strong>使用场景：</strong></p>
 * <ul>
 *   <li>监听应用启动的各个阶段</li>
 *   <li>在应用启动过程中执行自定义逻辑</li>
 *   <li>集成监控和日志系统</li>
 *   <li>实现启动性能统计</li>
 * </ul>
 *
 * <p><strong>注册目标：</strong><br>
 * {@code org.springframework.boot.SpringApplicationRunListener}</p>
 *
 * <p><strong>示例用法：</strong></p>
 * <pre>{@code
 * @AutoRunListener
 * public class CustomRunListener implements SpringApplicationRunListener {
 *     public CustomRunListener(SpringApplication application, String[] args) {
 *         // 构造函数必须包含这两个参数
 *     }
 *
 *     @Override
 *     public void starting() {
 *         // 应用开始启动时的处理逻辑
 *     }
 * }
 * }</pre>
 *
 * @author L.cm
 * @since 2.0.0
 * @see org.springframework.boot.SpringApplicationRunListener
 */
@Documented
@Retention(SOURCE)
@Target(TYPE)
public @interface AutoRunListener {
}
