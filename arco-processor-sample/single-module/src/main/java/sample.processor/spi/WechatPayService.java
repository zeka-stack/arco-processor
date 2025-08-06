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

package sample.processor.spi;

import dev.dong4j.zeka.processor.annotation.AutoService;

import java.util.UUID;

/**
 * 微信支付服务实现 - SPI 示例
 * 
 * <p>该类演示了同一个服务接口的另一个实现。两个实现类都会被注册到同一个配置文件中。</p>
 * 
 * <p><strong>生成的配置文件：</strong></p>
 * <pre>
 * META-INF/services/sample.processor.spi.PaymentService
 * 
 * 文件内容：
 * sample.processor.spi.AlipayService
 * sample.processor.spi.WechatPayService
 * </pre>
 *
 * @author L.cm
 * @since 2.0.0
 */
@AutoService(PaymentService.class)
public class WechatPayService implements PaymentService {
    
    /**
     * 处理微信支付
     * 
     * @param amount   支付金额
     * @param currency 货币类型
     * @return 支付结果
     */
    @Override
    public PaymentResult processPayment(double amount, String currency) {
        System.out.println("使用微信支付处理支付: " + amount + " " + currency);
        
        // 模拟支付处理逻辑
        boolean success = amount > 0 && "CNY".equalsIgnoreCase(currency);
        String message = success ? "微信支付成功" : "微信支付仅支持人民币";
        String transactionId = success ? "WECHAT_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase() : null;
        
        return new PaymentResult(success, message, transactionId);
    }
    
    /**
     * 获取支付方式名称
     * 
     * @return 支付方式名称
     */
    @Override
    public String getPaymentMethod() {
        return "微信支付";
    }
}