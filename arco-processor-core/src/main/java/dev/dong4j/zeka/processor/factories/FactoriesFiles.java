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

package dev.dong4j.zeka.processor.factories;

import dev.dong4j.zeka.processor.common.MultiSetMap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import javax.lang.model.util.Elements;
import javax.tools.FileObject;
import lombok.experimental.UtilityClass;

/**
 * Spring Boot 配置文件读写工具类
 *
 * <p>该工具类提供了 Spring Boot 相关配置文件的读取和写入功能，包括：</p>
 * <ul>
 *   <li>{@code META-INF/spring.factories} 文件的读写</li>
 *   <li>{@code META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports} 文件的读写</li>
 *   <li>支持增量编译时的配置合并</li>
 *   <li>自动验证配置类的有效性</li>
 * </ul>
 *
 * <p><strong>主要功能：</strong></p>
 * <ul>
 *   <li>解析 Properties 格式的 spring.factories 文件</li>
 *   <li>解析纯文本格式的 AutoConfiguration.imports 文件</li>
 *   <li>写入格式化的配置文件（支持换行和缩进）</li>
 *   <li>过滤无效的配置类（类不存在时自动排除）</li>
 * </ul>
 *
 * <p><strong>文件格式示例：</strong></p>
 * <pre>
 * # spring.factories 格式
 * org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
 *   com.example.config.DatabaseAutoConfiguration,\
 *   com.example.config.CacheAutoConfiguration
 *
 * # AutoConfiguration.imports 格式
 * com.example.config.DatabaseAutoConfiguration
 * com.example.config.CacheAutoConfiguration
 * </pre>
 *
 * @author L.cm
 * @since 2.0.0
 */
@SuppressWarnings("DuplicatedCode")
@UtilityClass
class FactoriesFiles {
    /** 默认字符编码 */
    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    /**
     * 读取 spring.factories 配置文件
     *
     * <p>解析 Properties 格式的 spring.factories 文件，提取配置键值对。
     * 会自动过滤掉不存在的类（通过 elementUtils 验证类的存在性）。</p>
     *
     * @param fileObject   要读取的文件对象
     * @param elementUtils 元素工具类，用于验证类是否存在
     * @return 包含配置信息的多值映射
     * @throws IOException 读取文件时发生的 IO 异常
     */
    static MultiSetMap<String, String> readFactoriesFile(FileObject fileObject, Elements elementUtils) throws IOException {
        // 读取并解析 Properties 格式的配置文件
        Properties properties = new Properties();
        try (InputStream input = fileObject.openInputStream()) {
            properties.load(input);
        }

        MultiSetMap<String, String> multiSetMap = new MultiSetMap<>();
        Set<Map.Entry<Object, Object>> entrySet = properties.entrySet();

        for (Map.Entry<Object, Object> objectEntry : entrySet) {
            String key = (String) objectEntry.getKey();
            String value = (String) objectEntry.getValue();
            if (value == null || value.trim().isEmpty()) {
                continue;
            }

            // 解析配置值（多个值用逗号分隔）
            String[] values = value.split(",");
            Set<String> valueSet = Arrays.stream(values)
                .filter(v -> !v.isEmpty())
                .map(String::trim)
                // 验证类是否存在，过滤掉已删除的类
                .filter((v) -> Objects.nonNull(elementUtils.getTypeElement(v)))
                .collect(Collectors.toSet());
            multiSetMap.putAll(key.trim(), valueSet);
        }
        return multiSetMap;
    }

    /**
     * 读取 AutoConfiguration.imports 配置文件
     *
     * <p>解析纯文本格式的 AutoConfiguration.imports 文件，每行一个配置类名。
     * 会自动过滤掉注释行（以 # 开头的行）和空行。</p>
     *
     * @param fileObject 要读取的文件对象
     * @return 包含自动配置类名的集合
     * @throws IOException 读取文件时发生的 IO 异常
     */
    static Set<String> readAutoConfigurationImports(FileObject fileObject) throws IOException {
        Set<String> set = new HashSet<>();
        try (
            InputStream input = fileObject.openInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input))
        ) {
            reader.lines()
                .map(String::trim)
                .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                .forEach(set::add);
        }
        return set;
    }

    /**
     * 写出 spring.factories 配置文件
     *
     * <p>将配置信息写入 Properties 格式的文件，使用反斜杠和换行符进行格式化，
     * 提高文件的可读性。每个配置项的值会用逗号分隔并适当缩进。</p>
     *
     * @param factories 要写入的配置信息
     * @param output    输出流
     * @throws IOException 写入文件时发生的 IO 异常
     */
    static void writeFactoriesFile(MultiSetMap<String, String> factories,
                                   OutputStream output) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, UTF_8));
        Set<String> keySet = factories.keySet();

        for (String key : keySet) {
            Set<String> values = factories.get(key);
            if (values == null || values.isEmpty()) {
                continue;
            }

            // 写入配置键
            writer.write(key);
            writer.write("=\\\n  ");

            // 写入配置值，使用逗号分隔并换行缩进
            StringJoiner joiner = new StringJoiner(",\\\n  ");
            for (String value : values) {
                joiner.add(value);
            }
            writer.write(joiner.toString());
            writer.newLine();
        }
        writer.flush();
    }

    /**
     * 写出 AutoConfiguration.imports 配置文件
     *
     * <p>将自动配置类名写入纯文本格式的文件，每行一个类名。
     * 这是 Spring Boot 2.7+ 推荐的新格式。</p>
     *
     * @param allAutoConfigurationImports 要写入的自动配置类名集合
     * @param output                      输出流
     * @throws IOException 写入文件时发生的 IO 异常
     */
    static void writeAutoConfigurationImportsFile(Set<String> allAutoConfigurationImports, OutputStream output) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, UTF_8));
        StringJoiner joiner = new StringJoiner("\n");

        for (String configurationImport : allAutoConfigurationImports) {
            joiner.add(configurationImport);
        }

        writer.write(joiner.toString());
        writer.flush();
    }

}
