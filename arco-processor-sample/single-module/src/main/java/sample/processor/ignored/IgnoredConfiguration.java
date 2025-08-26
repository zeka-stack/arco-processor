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

package sample.processor.ignored;

import dev.dong4j.zeka.processor.annotation.AutoIgnore;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.AutoConfiguration;

/**
 * 被忽略的配置类示例
 *
 * <p>该类演示了如何使用 {@code @AutoIgnore} 注解来防止类被注解处理器自动处理。
 * 虽然此类使用了 {@code @AutoConfiguration} 注解，但由于 {@code @AutoIgnore} 的存在，
 * 注解处理器会跳过对此类的处理，不会将其注册到任何配置文件中。</p>
 *
 * <p><strong>注意：</strong>被 {@code @AutoIgnore} 标记的类不会出现在：</p>
 * <ul>
 *   <li>{@code META-INF/spring.factories}</li>
 *   <li>{@code META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports}</li>
 * </ul>
 *
 * @author L.cm
 * @since 2.0.0
 */
@AutoIgnore
@AutoConfiguration
public class IgnoredConfiguration {

    /**
     * 这个 Bean 不会被自动配置机制发现
     * 只有在手动导入此配置类时才会生效
     *
     * @return 忽略的服务实例
     */
    @Bean
    public IgnoredService ignoredService() {
        return new IgnoredService();
    }

    /**
     * 被忽略的服务类
     */
    public static class IgnoredService {

        /**
         * 示例方法
         *
         * @return 服务标识
         */
        public String getServiceName() {
            return "This service is ignored by auto-configuration";
        }
    }
}
