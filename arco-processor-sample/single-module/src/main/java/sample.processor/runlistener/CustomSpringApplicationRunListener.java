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

package sample.processor.runlistener;

import dev.dong4j.zeka.processor.annotation.AutoRunListener;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.time.Duration;

/**
 * 自定义 Spring 应用运行监听器示例
 * 
 * <p>该类演示了如何使用 {@code @AutoRunListener} 注解创建 Spring 应用运行监听器。
 * 注解处理器会自动将此类注册到 {@code META-INF/spring.factories} 文件中。</p>
 * 
 * <p><strong>重要提示：</strong>SpringApplicationRunListener 的实现类必须提供一个
 * 接受 {@code SpringApplication} 和 {@code String[]} 参数的构造函数。</p>
 * 
 * <p><strong>注册配置：</strong></p>
 * <pre>
 * org.springframework.boot.SpringApplicationRunListener=\
 *   sample.processor.runlistener.CustomSpringApplicationRunListener
 * </pre>
 *
 * @author L.cm
 * @since 2.0.0
 */
@AutoRunListener
public class CustomSpringApplicationRunListener implements SpringApplicationRunListener {
    
    private final SpringApplication application;
    private final String[] args;
    
    /**
     * 构造函数 - 必须提供此构造函数
     * 
     * @param application Spring 应用实例
     * @param args        应用启动参数
     */
    public CustomSpringApplicationRunListener(SpringApplication application, String[] args) {
        this.application = application;
        this.args = args;
    }
    
    /**
     * 应用开始启动时调用
     * 
     * @param bootstrapContext 引导上下文
     */
    @Override
    public void starting(ConfigurableBootstrapContext bootstrapContext) {
        System.out.println("=== 自定义运行监听器：应用开始启动 ===");
    }
    
    /**
     * 环境准备完成时调用
     * 
     * @param bootstrapContext 引导上下文
     * @param environment      环境配置
     */
    @Override
    public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment environment) {
        System.out.println("=== 自定义运行监听器：环境准备完成 ===");
        System.out.println("应用名称: " + environment.getProperty("spring.application.name", "unknown"));
    }
    
    /**
     * 应用上下文准备完成时调用
     * 
     * @param context 应用上下文
     */
    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        System.out.println("=== 自定义运行监听器：应用上下文准备完成 ===");
    }
    
    /**
     * 应用上下文加载完成时调用
     * 
     * @param context 应用上下文
     */
    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        System.out.println("=== 自定义运行监听器：应用上下文加载完成 ===");
    }
    
    /**
     * 应用启动完成时调用
     * 
     * @param context  应用上下文
     * @param timeTaken 启动耗时
     */
    @Override
    public void started(ConfigurableApplicationContext context, Duration timeTaken) {
        System.out.println("=== 自定义运行监听器：应用启动完成 ===");
        System.out.println("启动耗时: " + timeTaken.toMillis() + "ms");
    }
    
    /**
     * 应用就绪时调用
     * 
     * @param context  应用上下文
     * @param timeTaken 启动耗时
     */
    @Override
    public void ready(ConfigurableApplicationContext context, Duration timeTaken) {
        System.out.println("=== 自定义运行监听器：应用就绪 ===");
        System.out.println("总耗时: " + timeTaken.toMillis() + "ms");
    }
    
    /**
     * 应用启动失败时调用
     * 
     * @param context   应用上下文
     * @param exception 异常信息
     */
    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        System.err.println("=== 自定义运行监听器：应用启动失败 ===");
        System.err.println("失败原因: " + exception.getMessage());
    }
}