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

package sample.processor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sample.processor.config.AutoConfigurationSample;
import sample.processor.spi.PaymentService;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * 示例控制器
 * 
 * <p>该控制器演示了如何使用各种注解处理器生成的配置和 SPI 服务。</p>
 *
 * @author L.cm
 * @since 2.0.0
 */
@RestController
@RequestMapping("/sample")
public class SampleController {
    
    @Autowired
    private AutoConfigurationSample.SampleService sampleService;
    
    @Autowired
    private Environment environment;
    
    /**
     * 测试自动配置的服务
     */
    @GetMapping("/config")
    public Map<String, Object> testAutoConfiguration(@RequestParam(defaultValue = "Hello World") String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("original", message);
        result.put("processed", sampleService.processMessage(message));
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }
    
    /**
     * 测试环境配置
     */
    @GetMapping("/env")
    public Map<String, Object> testEnvironment() {
        Map<String, Object> result = new HashMap<>();
        result.put("custom-property", environment.getProperty("sample.processor.env.custom-property"));
        result.put("processing-time", environment.getProperty("sample.processor.env.processing-time"));
        result.put("processor-name", environment.getProperty("sample.processor.env.processor-name"));
        result.put("initializer-enabled", environment.getProperty("sample.processor.initializer.enabled"));
        result.put("initializer-message", environment.getProperty("sample.processor.initializer.message"));
        return result;
    }
    
    /**
     * 测试 SPI 服务
     */
    @GetMapping("/payment")
    public Map<String, Object> testSpiServices(@RequestParam(defaultValue = "100") double amount,
                                              @RequestParam(defaultValue = "CNY") String currency) {
        Map<String, Object> result = new HashMap<>();
        
        ServiceLoader<PaymentService> serviceLoader = ServiceLoader.load(PaymentService.class);
        for (PaymentService paymentService : serviceLoader) {
            PaymentService.PaymentResult paymentResult = paymentService.processPayment(amount, currency);
            result.put(paymentService.getPaymentMethod(), paymentResult);
        }
        
        return result;
    }
    
    /**
     * 触发异常测试故障分析器
     */
    @GetMapping("/error")
    public String triggerError(@RequestParam(required = false) String param) {
        if (param == null || param.trim().isEmpty()) {
            throw new IllegalArgumentException("参数不能为空");
        }
        return "参数正常: " + param;
    }
}