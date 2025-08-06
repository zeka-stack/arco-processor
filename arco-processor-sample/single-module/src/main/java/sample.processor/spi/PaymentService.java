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

/**
 * 支付服务接口 - SPI 示例
 * 
 * <p>该接口用于演示 Java SPI 机制。通过 {@code @AutoService} 注解，
 * 可以自动注册服务提供者实现类。</p>
 *
 * @author L.cm
 * @since 2.0.0
 */
public interface PaymentService {
    
    /**
     * 处理支付
     * 
     * @param amount 支付金额
     * @param currency 货币类型
     * @return 支付结果
     */
    PaymentResult processPayment(double amount, String currency);
    
    /**
     * 获取支付方式名称
     * 
     * @return 支付方式名称
     */
    String getPaymentMethod();
    
    /**
     * 支付结果
     */
    class PaymentResult {
        private final boolean success;
        private final String message;
        private final String transactionId;
        
        public PaymentResult(boolean success, String message, String transactionId) {
            this.success = success;
            this.message = message;
            this.transactionId = transactionId;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public String getTransactionId() {
            return transactionId;
        }
        
        @Override
        public String toString() {
            return String.format("PaymentResult{success=%s, message='%s', transactionId='%s'}", 
                    success, message, transactionId);
        }
    }
}