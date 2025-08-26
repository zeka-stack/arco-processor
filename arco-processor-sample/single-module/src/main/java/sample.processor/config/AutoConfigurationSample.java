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

package sample.processor.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.AutoConfiguration;

/**
 * 自动配置示例
 *
 * <p>该类演示了如何使用 {@code @AutoConfiguration} 注解创建自动配置类。
 * 注解处理器会自动检测此类并将其注册到相应的配置文件中。</p>
 *
 * <p><strong>注意：</strong>当类被 {@code @AutoConfiguration} 注解标记时，
 * 注解处理器会：</p>
 * <ul>
 *   <li>将此类添加到 {@code META-INF/spring.factories} 文件</li>
 *   <li>将此类添加到 {@code META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports} 文件</li>
 * </ul>
 *
 * @author L.cm
 * @since 2.0.0
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "sample.processor", name = "enabled", havingValue = "true", matchIfMissing = true)
public class AutoConfigurationSample {

    /**
     * 示例服务 Bean
     *
     * @return SampleService 实例
     */
    @Bean
    public SampleService sampleService() {
        return new SampleService();
    }

    /**
     * 示例服务实现类
     */
    public static class SampleService {

        /**
         * 示例方法
         *
         * @param message 消息内容
         * @return 处理后的消息
         */
        public String processMessage(String message) {
            return "Processed: " + message;
        }
    }
}
