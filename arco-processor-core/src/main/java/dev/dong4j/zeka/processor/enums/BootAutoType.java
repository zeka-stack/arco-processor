package dev.dong4j.zeka.processor.enums;

import dev.dong4j.zeka.processor.annotation.AutoContextInitializer;
import dev.dong4j.zeka.processor.annotation.AutoEnvPostProcessor;
import dev.dong4j.zeka.processor.annotation.AutoFailureAnalyzer;
import dev.dong4j.zeka.processor.annotation.AutoListener;
import dev.dong4j.zeka.processor.annotation.AutoRunListener;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Description: 注解类型</p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:29
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum BootAutoType {
    /** ApplicationContextInitializer 添加到 spring.factories */
    CONTEXT_INITIALIZER(AutoContextInitializer.class.getName(), "org.springframework.context.ApplicationContextInitializer"),
    /** ApplicationListener 添加到 spring.factories */
    LISTENER(AutoListener.class.getName(), "org.springframework.context.ApplicationListener"),
    /** SpringApplicationRunListener 添加到 spring.factories */
    RUN_LISTENER(AutoRunListener.class.getName(), "org.springframework.boot.SpringApplicationRunListener"),
    /** EnvironmentPostProcessor 添加到 spring.factories */
    ENV_POST_PROCESSOR(AutoEnvPostProcessor.class.getName(), "org.springframework.boot.env.EnvironmentPostProcessor"),
    /** FailureAnalyzer 添加到 spring.factories */
    FAILURE_ANALYZER(AutoFailureAnalyzer.class.getName(), "org.springframework.boot.diagnostics.FailureAnalyzer"),
    /** Component boot auto type */
    COMPONENT("org.springframework.stereotype.Component", "org.springframework.boot.autoconfigure.EnableAutoConfiguration");

    /** Annotation */
    private final String annotation;
    /** Configure key */
    private final String configureKey;

}
