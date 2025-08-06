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
 * 自动数据库初始化依赖检测器注解
 *
 * <p>该注解用于标记实现了 {@code org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitializationDetector} 接口的类，
 * 注解处理器会自动将被标记的类注册到 {@code META-INF/spring.factories} 文件中，
 * 使其在 Spring Boot 应用启动时被自动发现和使用。</p>
 *
 * <p><strong>使用场景：</strong></p>
 * <ul>
 *   <li>检测 Bean 是否依赖于数据库初始化</li>
 *   <li>确保正确的启动顺序和依赖关系</li>
 *   <li>自定义数据库初始化依赖检测逻辑</li>
 *   <li>与数据库迁移工具集成</li>
 * </ul>
 *
 * <p><strong>注册目标：</strong><br>
 * {@code org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitializationDetector}</p>
 *
 * <p><strong>示例用法：</strong></p>
 * <pre>{@code
 * @AutoDependsOnDatabaseInitializationDetector
 * public class CustomDependsOnDatabaseInitializationDetector implements DependsOnDatabaseInitializationDetector {
 *     @Override
 *     public boolean detect(RegisteredBean registeredBean) {
 *         // 检测 Bean 是否依赖数据库初始化
 *         return registeredBean.getBeanClass().isAnnotationPresent(RequiresDatabaseInit.class);
 *     }
 * }
 * }</pre>
 *
 * @author L.cm
 * @see org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitializationDetector
 * @since 2.0.0
 */
@Documented
@Retention(SOURCE)
@Target(TYPE)
public @interface AutoDependsOnDatabaseInitializationDetector {
}
