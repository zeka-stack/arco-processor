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

package sample.processor.initializer;

import dev.dong4j.zeka.processor.annotation.AutoContextInitializer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义应用上下文初始化器示例
 * 
 * <p>该类演示了如何使用 {@code @AutoContextInitializer} 注解创建应用上下文初始化器。
 * 注解处理器会自动将此类注册到 {@code META-INF/spring.factories} 文件中。</p>
 * 
 * <p><strong>注册配置：</strong></p>
 * <pre>
 * org.springframework.context.ApplicationContextInitializer=\
 *   sample.processor.initializer.CustomContextInitializer
 * </pre>
 *
 * @author L.cm
 * @since 2.0.0
 */
@AutoContextInitializer
public class CustomContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    
    /**
     * 初始化应用上下文
     * 
     * @param applicationContext 可配置的应用上下文
     */
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        System.out.println("=== 自定义上下文初始化器被调用 ===");
        
        // 添加自定义属性源
        Map<String, Object> customProperties = new HashMap<>();
        customProperties.put("sample.processor.initializer.enabled", "true");
        customProperties.put("sample.processor.initializer.message", "Hello from CustomContextInitializer");
        
        MapPropertySource propertySource = new MapPropertySource("customInitializerProperties", customProperties);
        applicationContext.getEnvironment().getPropertySources().addLast(propertySource);
        
        System.out.println("已添加自定义属性源到环境中");
    }
}