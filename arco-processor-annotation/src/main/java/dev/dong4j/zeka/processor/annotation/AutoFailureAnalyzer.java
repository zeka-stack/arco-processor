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
 * 自动故障分析器注解
 *
 * <p>该注解用于标记实现了 {@code org.springframework.boot.diagnostics.FailureAnalyzer} 接口的类，
 * 注解处理器会自动将被标记的类注册到 {@code META-INF/spring.factories} 文件中，
 * 使其在应用启动失败时被自动发现和执行，提供友好的错误分析信息。</p>
 *
 * <p><strong>使用场景：</strong></p>
 * <ul>
 *   <li>自定义异常的友好错误提示</li>
 *   <li>提供问题解决建议</li>
 *   <li>集成外部诊断工具</li>
 *   <li>改善开发者调试体验</li>
 * </ul>
 *
 * <p><strong>注册目标：</strong><br>
 * {@code org.springframework.boot.diagnostics.FailureAnalyzer}</p>
 *
 * <p><strong>示例用法：</strong></p>
 * <pre>{@code
 * @AutoFailureAnalyzer
 * public class CustomFailureAnalyzer extends AbstractFailureAnalyzer<CustomException> {
 *     @Override
 *     protected FailureAnalysis analyze(Throwable rootFailure, CustomException cause) {
 *         return new FailureAnalysis("自定义错误描述", "解决建议", cause);
 *     }
 * }
 * }</pre>
 *
 * @author L.cm
 * @since 1.0.0
 * @see org.springframework.boot.diagnostics.FailureAnalyzer
 */
@Documented
@Retention(SOURCE)
@Target(TYPE)
public @interface AutoFailureAnalyzer {
}
