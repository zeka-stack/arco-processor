package dev.dong4j.zeka.processor;

import dev.dong4j.zeka.processor.util.ProcessorUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:26
 * @since 1.0.0
 */
@Slf4j
class ProcessorUtilsTest {

    /**
     * Test
     *
     * @since 1.0.0
     */
    @Test
    void test() {
        log.info("{}", ProcessorUtils.isPresent("org.springframework.boot.SpringApplication"));
        log.info("{}", ProcessorUtils.isPresent("org.springframework.boot.autoconfigure.SpringBootApplication"));
        log.info("{}", ProcessorUtils.isPresent("org.springframework.cloud.client.SpringCloudApplication"));
    }
}
