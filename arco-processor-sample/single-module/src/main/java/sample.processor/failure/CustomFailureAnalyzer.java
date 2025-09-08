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

package sample.processor.failure;

import dev.dong4j.zeka.processor.annotation.AutoFailureAnalyzer;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

/**
 * 自定义故障分析器示例
 *
 * <p>该类演示了如何使用 {@code @AutoFailureAnalyzer} 注解创建故障分析器。
 * 注解处理器会自动将此类注册到 {@code META-INF/spring.factories} 文件中。</p>
 *
 * <p><strong>注册配置：</strong></p>
 * <pre>
 * org.springframework.boot.diagnostics.FailureAnalyzer=\
 *   sample.processor.failure.CustomFailureAnalyzer
 * </pre>
 *
 * @author L.cm
 * @since 1.0.0
 */
@AutoFailureAnalyzer
public class CustomFailureAnalyzer extends AbstractFailureAnalyzer<IllegalArgumentException> {

    /**
     * 分析指定类型的故障
     *
     * @param rootFailure 根异常
     * @param cause       导致故障的具体异常
     * @return 故障分析结果
     */
    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, IllegalArgumentException cause) {
        String description = "检测到非法参数异常: " + cause.getMessage();

        String action = "建议的解决方案:\n" +
                       "1. 检查传入的参数是否符合要求\n" +
                       "2. 验证配置文件中的参数设置\n" +
                       "3. 确保参数不为空且格式正确\n" +
                       "4. 查看相关文档获取参数的有效值范围";

        return new FailureAnalysis(description, action, cause);
    }
}
