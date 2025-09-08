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

package sample.processor.env;

import dev.dong4j.zeka.processor.annotation.AutoEnvPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义环境后置处理器示例
 *
 * <p>该类演示了如何使用 {@code @AutoEnvPostProcessor} 注解创建环境后置处理器。
 * 注解处理器会自动将此类注册到 {@code META-INF/spring.factories} 文件中。</p>
 *
 * <p><strong>注册配置：</strong></p>
 * <pre>
 * org.springframework.boot.env.EnvironmentPostProcessor=\
 *   sample.processor.env.CustomEnvironmentPostProcessor
 * </pre>
 *
 * @author L.cm
 * @since 1.0.0
 */
@AutoEnvPostProcessor
public class CustomEnvironmentPostProcessor implements EnvironmentPostProcessor {

    /**
     * 后置处理环境配置
     *
     * @param environment 可配置的环境
     * @param application Spring 应用实例
     */
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        System.out.println("=== 自定义环境后置处理器被调用 ===");

        // 添加自定义配置属性
        Map<String, Object> customProperties = new HashMap<>();
        customProperties.put("sample.processor.env.custom-property", "value-from-env-processor");
        customProperties.put("sample.processor.env.processing-time", System.currentTimeMillis());
        customProperties.put("sample.processor.env.processor-name", "CustomEnvironmentPostProcessor");

        MapPropertySource propertySource = new MapPropertySource("customEnvProcessor", customProperties);
        environment.getPropertySources().addFirst(propertySource);

        System.out.println("已添加自定义环境配置属性");

        // 打印当前激活的 profiles
        String[] activeProfiles = environment.getActiveProfiles();
        if (activeProfiles.length > 0) {
            System.out.println("当前激活的 Profiles: " + String.join(", ", activeProfiles));
        } else {
            System.out.println("当前没有激活的 Profiles");
        }
    }
}
