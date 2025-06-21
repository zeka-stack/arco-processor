package dev.dong4j.zeka.processor.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.04.22 16:00
 * @since 1.0.0
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface AutoService {
    /**
     * Returns the interfaces implemented by this service provider.
     *
     * @return interface array
     * @since 1.0.0
     */
    Class<?>[] value();
}
