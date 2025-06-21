package dev.dong4j.zeka.processor.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>Description: </p>
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:27
 * @since 1.0.0
 */
public class MultiSetMap<K, V> {
    /** Map */
    private final Map<K, Set<V>> map;

    /**
     * Instantiates a new Multi set map.
     *
     * @since 1.0.0
     */
    @Contract(pure = true)
    public MultiSetMap() {
        this.map = new HashMap<>(16);
    }

    /**
     * put to MultiSetMap
     *
     * @param key   键
     * @param value 值
     * @return boolean boolean
     * @since 1.0.0
     */
    public boolean put(K key, V value) {
        Set<V> set = this.map.get(key);
        if (set == null) {
            set = this.createSet();
            if (set.add(value)) {
                this.map.put(key, set);
                return true;
            } else {
                throw new AssertionError("New set violated the set spec");
            }
        } else {
            return set.add(value);
        }
    }

    /**
     * Create set set
     *
     * @return the set
     * @since 1.0.0
     */
    @NotNull
    @Contract(value = " -> new", pure = true)
    private Set<V> createSet() {
        return new HashSet<>();
    }

    /**
     * 是否包含某个key
     *
     * @param key key
     * @return 结果 boolean
     * @since 1.0.0
     */
    public boolean containsKey(K key) {
        return this.map.containsKey(key);
    }

    /**
     * 是否包含 value 中的某个值
     *
     * @param value value
     * @return 是否包含 boolean
     * @since 1.0.0
     */
    public boolean containsVal(V value) {
        Collection<Set<V>> values = this.map.values();
        return values.stream().anyMatch(vs -> vs.contains(value));
    }

    /**
     * key 集合
     *
     * @return keys set
     * @since 1.0.0
     */
    public Set<K> keySet() {
        return this.map.keySet();
    }

    /**
     * put list to MultiSetMap
     *
     * @param key 键
     * @param set 值列表
     * @return boolean boolean
     * @since 1.0.0
     */
    public boolean putAll(K key, Set<V> set) {
        if (set == null) {
            return false;
        } else {
            this.map.put(key, set);
            return true;
        }
    }

    /**
     * get List by key
     *
     * @param key 键
     * @return List set
     * @since 1.0.0
     */
    public Set<V> get(K key) {
        return this.map.get(key);
    }

    /**
     * clear MultiSetMap
     *
     * @since 1.0.0
     */
    public void clear() {
        this.map.clear();
    }

    /**
     * isEmpty
     *
     * @return isEmpty boolean
     * @since 1.0.0
     */
    public boolean isEmpty() {
        return this.map.isEmpty();
    }
}
