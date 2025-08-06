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

package dev.dong4j.zeka.processor.aot;

import dev.dong4j.zeka.processor.common.MultiSetMap;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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
 * spring boot 自动化配置工具类
 *
 * @author L.cm
 */
@SuppressWarnings("DuplicatedCode")
@UtilityClass
class FactoriesFiles {
    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    /**
     * 读取 spring.factories 文件
     *
     * @param fileObject FileObject
     * @return MultiSetMap
     * @throws IOException 异常信息
     */
    static MultiSetMap<String, String> readFactoriesFile(FileObject fileObject, Elements elementUtils) throws IOException {
        // 读取 spring.factories 内容
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
            // 解析 spring.factories
            String[] values = value.split(",");
            Set<String> valueSet = Arrays.stream(values)
                .filter(v -> !v.isEmpty())
                .map(String::trim)
                // 校验是否删除文件
                .filter((v) -> Objects.nonNull(elementUtils.getTypeElement(v)))
                .collect(Collectors.toSet());
            multiSetMap.putAll(key.trim(), valueSet);
        }
        return multiSetMap;
    }

    /**
     * 写出 spring.factories 文件
     *
     * @param factories factories 信息
     * @param output    输出流
     * @throws IOException 异常信息
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
            writer.write(key);
            writer.write("=\\\n  ");
            StringJoiner joiner = new StringJoiner(",\\\n  ");
            for (String value : values) {
                joiner.add(value);
            }
            writer.write(joiner.toString());
            writer.newLine();
        }
        writer.flush();
    }

}
