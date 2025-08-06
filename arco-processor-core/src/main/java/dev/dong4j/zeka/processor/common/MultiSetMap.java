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

package dev.dong4j.zeka.processor.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 多值集合映射工具类
 *
 * <p>该类实现了一个键可以对应多个值的映射结构，底层使用 {@code Map<K, Set<V>>} 存储。
 * 主要用于注解处理器中收集和管理配置信息，例如一个配置键对应多个实现类。</p>
 *
 * <p><strong>主要特性：</strong></p>
 * <ul>
 *   <li>一个键可以对应多个不重复的值（使用 Set 去重）</li>
 *   <li>支持批量添加和合并操作</li>
 *   <li>提供便捷的包含性检查方法</li>
 *   <li>线程不安全，适合单线程环境使用</li>
 * </ul>
 *
 * <p><strong>使用场景：</strong></p>
 * <ul>
 *   <li>收集 spring.factories 配置项</li>
 *   <li>管理 SPI 服务提供者映射</li>
 *   <li>分组管理注解处理结果</li>
 * </ul>
 *
 * <p><strong>示例用法：</strong></p>
 * <pre>{@code
 * MultiSetMap<String, String> map = new MultiSetMap<>();
 * map.put("service.interface", "service.impl1");
 * map.put("service.interface", "service.impl2");
 * Set<String> impls = map.get("service.interface"); // [service.impl1, service.impl2]
 * }</pre>
 *
 * @param <K> 键的类型
 * @param <V> 值的类型
 * @author L.cm
 * @since 2.0.0
 */
@SuppressWarnings("UnusedReturnValue")
public class MultiSetMap<K, V> {
    /** 底层存储结构，使用 Map 存储键值对，值为 Set 集合 */
    private transient final Map<K, Set<V>> map;

    /** 构造函数，初始化底层 Map */
    public MultiSetMap() {
        map = new HashMap<>();
    }

    /** 创建新的值集合 */
    private Set<V> createSet() {
        return new HashSet<>();
    }

    /**
     * 向多值映射中添加键值对
     *
     * <p>如果键不存在，会创建新的值集合；如果键已存在，会将值添加到现有集合中。
     * 由于使用 Set 存储值，重复的值不会被添加。</p>
     *
     * @param key   要添加的键
     * @param value 要添加的值
     * @return 如果值被成功添加（即之前不存在）返回 {@code true}，否则返回 {@code false}
     */
    public boolean put(K key, V value) {
        Set<V> set = map.get(key);
        if (set == null) {
            set = createSet();
            if (set.add(value)) {
                map.put(key, set);
                return true;
            } else {
                throw new AssertionError("New set violated the set spec");
            }
        } else {
            return set.add(value);
        }
    }

    /**
     * 检查是否包含指定的键
     *
     * @param key 要检查的键
     * @return 如果包含该键返回 {@code true}，否则返回 {@code false}
     */
    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    /**
     * 检查是否包含指定的值
     *
     * <p>该方法会遍历所有值集合，检查是否有任何集合包含指定的值。</p>
     *
     * @param value 要检查的值
     * @return 如果任何值集合包含该值返回 {@code true}，否则返回 {@code false}
     */
    public boolean containsVal(V value) {
        Collection<Set<V>> values = map.values();
        return values.stream().anyMatch(vs -> vs.contains(value));
    }

    /**
     * 获取所有键的集合
     *
     * @return 包含所有键的 Set 集合
     */
    public Set<K> keySet() {
        return map.keySet();
    }

    /**
     * put list to MultiSetMap
     *
     * @param key 键
     * @param set 值列表
     * @return boolean
     */
    public boolean putAll(K key, Set<V> set) {
        if (set == null) {
            return false;
        }
        Set<V> vSet = map.computeIfAbsent(key, k -> createSet());
        vSet.addAll(set);
        return true;
    }

    /**
     * put MultiSetMap to MultiSetMap
     *
     * @param data MultiSetMap
     * @return boolean
     */
    public boolean putAll(MultiSetMap<K, V> data) {
        if (data == null || data.isEmpty()) {
            return false;
        } else {
            for (K k : data.keySet()) {
                this.putAll(k, data.get(k));
            }
            return true;
        }
    }

    /**
     * get List by key
     *
     * @param key 键
     * @return List
     */
    public Set<V> get(K key) {
        return map.get(key);
    }

    /**
     * clear MultiSetMap
     */
    public void clear() {
        map.clear();
    }

    /**
     * isEmpty
     *
     * @return isEmpty
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public String toString() {
        return map.toString();
    }

}
