/*
 * Copyright 2008 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.dong4j.zeka.processor.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Java SPI 服务自动注册注解
 *
 * <p>该注解用于标记 Java SPI 服务提供者实现类，如 {@link java.util.ServiceLoader} 所描述。
 * 注解处理器会自动生成相应的配置文件，使服务提供者能够通过 {@link java.util.ServiceLoader#load(Class)} 被加载。</p>
 *
 * <p><strong>服务提供者必须符合以下规范：</strong></p>
 * <ul>
 *   <li>必须是非内部类、非匿名类的具体类</li>
 *   <li>必须有公开可访问的无参构造函数</li>
 *   <li>必须实现 {@code value()} 方法返回的接口类型</li>
 * </ul>
 *
 * <p><strong>使用场景：</strong></p>
 * <ul>
 *   <li>实现插件化架构</li>
 *   <li>提供可扩展的服务接口</li>
 *   <li>模块间的松耦合集成</li>
 *   <li>第三方库的服务发现</li>
 * </ul>
 *
 * <p><strong>生成位置：</strong><br>
 * {@code META-INF/services/[接口全限定名]}</p>
 *
 * <p><strong>示例用法：</strong></p>
 * <pre>{@code
 * // 定义服务接口
 * public interface PaymentService {
 *     void processPayment(Payment payment);
 * }
 *
 * // 实现服务提供者
 * @AutoService(PaymentService.class)
 * public class AlipayService implements PaymentService {
 *     @Override
 *     public void processPayment(Payment payment) {
 *         // 支付宝支付实现
 *     }
 * }
 *
 * // 使用服务
 * ServiceLoader<PaymentService> loader = ServiceLoader.load(PaymentService.class);
 * for (PaymentService service : loader) {
 *     service.processPayment(payment);
 * }
 * }</pre>
 *
 * @author google (original), L.cm (enhanced)
 * @since 2.0.0
 * @see java.util.ServiceLoader
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface AutoService {
    /**
     * Returns the interfaces implemented by this service provider.
     *
     * @return interface array
     */
    Class<?>[] value();
}
