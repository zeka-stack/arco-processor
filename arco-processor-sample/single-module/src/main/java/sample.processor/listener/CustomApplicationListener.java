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

package sample.processor.listener;

import dev.dong4j.zeka.processor.annotation.AutoListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

/**
 * 自定义应用监听器示例
 *
 * <p>该类演示了如何使用 {@code @AutoListener} 注解创建应用监听器。
 * 注解处理器会自动将此类注册到 {@code META-INF/spring.factories} 文件中，
 * 使其在应用启动时被自动发现和注册。</p>
 *
 * <p><strong>注册配置：</strong></p>
 * <pre>
 * org.springframework.context.ApplicationListener=\
 *   sample.processor.listener.CustomApplicationListener
 * </pre>
 *
 * @author L.cm
 * @since 1.0.0
 */
@AutoListener
public class CustomApplicationListener implements ApplicationListener<ApplicationReadyEvent> {

    /**
     * 处理应用就绪事件
     *
     * @param event 应用就绪事件
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        System.out.println("=== 应用已就绪，自定义监听器被触发 ===");
        System.out.println("应用名称: " + event.getApplicationContext().getApplicationName());
        System.out.println("启动时间: " + event.getTimeTaken().toMillis() + "ms");
    }
}
