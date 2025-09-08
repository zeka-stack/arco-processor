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

package dev.dong4j.zeka.processor.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.lang.model.util.Elements;
import javax.tools.FileObject;
import lombok.experimental.UtilityClass;

/**
 * Java SPI 服务配置文件读写工具类
 *
 * <p>该工具类提供了读取和写入 Java SPI 服务配置文件的功能。SPI 配置文件位于
 * {@code META-INF/services/} 目录下，文件名为服务接口的全限定名，内容为
 * 服务提供者实现类的全限定名列表（每行一个）。</p>
 *
 * <p><strong>主要功能：</strong></p>
 * <ul>
 *   <li>读取现有的 SPI 服务配置文件</li>
 *   <li>写入新的 SPI 服务配置文件</li>
 *   <li>自动过滤注释行和无效的类名</li>
 *   <li>验证服务提供者类的存在性</li>
 * </ul>
 *
 * <p><strong>文件格式：</strong></p>
 * <pre>
 * # 注释行（以 # 开头）
 * com.example.ServiceImpl1
 * com.example.ServiceImpl2
 * </pre>
 *
 * @author L.cm
 * @since 1.0.0
 */
@UtilityClass
class ServicesFiles {
    /** 默认字符编码 */
    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    /**
     * 读取 SPI 服务配置文件中的服务类集合
     *
     * <p>解析纯文本格式的 SPI 配置文件，提取服务提供者类名。
     * 会自动过滤注释行、空行和不存在的类。</p>
     *
     * @param fileObject   要读取的服务配置文件，读取后会自动关闭
     * @param elementUtils 元素工具类，用于验证类是否存在
     * @return 包含有效服务类名的集合，不会为 {@code null}
     * @throws IOException 读取文件时发生的 IO 异常
     */
    static Set<String> readServiceFile(FileObject fileObject, Elements elementUtils) throws IOException {
        HashSet<String> serviceClasses = new HashSet<>();
        try (
            InputStream input = fileObject.openInputStream();
            InputStreamReader isr = new InputStreamReader(input, UTF_8);
            BufferedReader r = new BufferedReader(isr)
        ) {
            String line;
            while ((line = r.readLine()) != null) {
                // 跳过注释行（包含 # 字符的行）
                int commentStart = line.indexOf('#');
                if (commentStart >= 0) {
                    continue;
                }
                line = line.trim();
                // 验证类是否存在，过滤掉已删除的类
                if (!line.isEmpty() && Objects.nonNull(elementUtils.getTypeElement(line))) {
                    serviceClasses.add(line);
                }
            }
            return serviceClasses;
        }
    }

    /**
     * 写入服务类名集合到 SPI 服务配置文件
     *
     * <p>将服务提供者类名写入纯文本格式的文件，每行一个类名。
     * 输出流使用后不会自动关闭。</p>
     *
     * @param services 要写入的服务类名集合，不能为 {@code null}
     * @param output   输出流，不能为 {@code null}，使用后不会被关闭
     * @throws IOException 写入文件时发生的 IO 异常
     */
    static void writeServiceFile(Collection<String> services, OutputStream output) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, UTF_8));
        for (String service : services) {
            writer.write(service);
            writer.newLine();
        }
        writer.flush();
    }
}
