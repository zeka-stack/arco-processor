package dev.dong4j.zeka.processor.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * <p>Description: EnvironmentPostProcessor 处理 </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.04.12 15:16
 * @since 1.0.0
 */
@Documented
@Retention(SOURCE)
@Target(TYPE)
public @interface AutoEnvPostProcessor {
}
