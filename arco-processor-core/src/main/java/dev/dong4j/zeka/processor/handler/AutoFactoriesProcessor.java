package dev.dong4j.zeka.processor.handler;

import com.google.auto.service.AutoService;
import dev.dong4j.zeka.processor.ArcoAbstractProcessor;
import dev.dong4j.zeka.processor.enums.BootAutoType;
import dev.dong4j.zeka.processor.util.MultiSetMap;
import dev.dong4j.zeka.processor.util.ProcessorUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>Description: 注解处理器, 根据注解写入 spring.factories 自动配置类, 不需要手动写入 </p>
 * 使用 google 的 autoservice 技术, 自动生成 SPI 需要的文件, 当被依赖时, 会通过 SPI 调用 此类, 然后进行注解处理, 生成需要的文件
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.26 21:45
 * @since 1.0.0
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes("*")
@SupportedOptions("debug")
public class AutoFactoriesProcessor extends ArcoAbstractProcessor {
    /** 处理的注解 @FeignClient */
    private static final String FEIGN_CLIENT_ANNOTATION = "org.springframework.cloud.openfeign.FeignClient";
    /** Feign 自动配置 */
    private static final String FEIGN_AUTO_CONFIGURE_KEY = "dev.dong4j.zeka.starter.feign.autoconfigure.FeignClientAutoConfiguration";
    /** 启动类父类 */
    private static final String START_CLASS_NAME = "dev.dong4j.zeka.starter.launcher.ZekaStarter";
    /** 用于生成 app.pid 文件 */
    private static final String APPLICATION_PID_FILE_WRITER = "org.springframework.boot.context.ApplicationPidFileWriter";
    /** 是否存在启动类 */
    private boolean existStartClass = false;
    /** 元素辅助类 */
    private Elements elementUtils;
    /** 数据承载 */
    private final MultiSetMap<String, String> factories = new MultiSetMap<>();

    /**
     * Init *
     *
     * @param processingEnv processing env
     * @since 1.0.0
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.elementUtils = processingEnv.getElementUtils();
    }

    /**
     * Process boolean
     *
     * @param annotations annotations
     * @param roundEnv    round env
     * @return the boolean
     * @since 1.0.0
     */
    @Override
    protected boolean processImpl(Set<? extends TypeElement> annotations, @NotNull RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            this.generateFactoriesFiles();
        } else {
            this.processAnnotations(annotations, roundEnv);
        }
        return false;
    }

    /**
     * Process annotations *
     *
     * @param annotations annotations
     * @param roundEnv    round env
     * @since 1.0.0
     */
    @SuppressWarnings("java:S3776")
    private void processAnnotations(@NotNull Set<? extends TypeElement> annotations, @NotNull RoundEnvironment roundEnv) {
        // 日志 打印信息 gradle build --debug
        this.log(annotations.toString());
        Set<? extends Element> elementSet = roundEnv.getRootElements();
        this.log("All Element set: " + elementSet.toString());

        // 过滤 TypeElement, elementSet 为处理的 class
        Set<TypeElement> typeElementSet = elementSet.stream()
            .filter(this::isClassOrInterface)
            .filter(TypeElement.class::isInstance)
            .map(TypeElement.class::cast)
            .collect(Collectors.toSet());
        // 如果为空直接跳出
        if (typeElementSet.isEmpty()) {
            this.log("Annotations elementSet is isEmpty");
            return;
        }

        typeElementSet.forEach(typeElement -> {
            if (!this.existStartClass && typeElement.getSuperclass() != null && START_CLASS_NAME.equals(typeElement.getSuperclass().toString())) {
                this.existStartClass = true;
            }
            if (this.isAnnotation(this.elementUtils, typeElement, FEIGN_CLIENT_ANNOTATION)) {
                this.log("Found @FeignClient Element: " + typeElement.toString());

                ElementKind elementKind = typeElement.getKind();
                // Feign Client 只处理 接口
                if (ElementKind.INTERFACE != elementKind) {
                    this.fatalError("@FeignClient Element " + typeElement + " 不是接口. ");
                    return;
                }

                String factoryName = typeElement.getQualifiedName().toString();
                if (this.factories.containsVal(factoryName)) {
                    return;
                }

                this.log("读取到新配置 spring.factories factoryName: " + factoryName);
                this.factories.put(FEIGN_AUTO_CONFIGURE_KEY, factoryName);
            } else {
                for (BootAutoType autoType : BootAutoType.values()) {
                    String annotation = autoType.getAnnotation();
                    if (this.isAnnotation(this.elementUtils, typeElement, annotation)) {
                        this.log("Found @" + annotation + " Element: " + typeElement.toString());

                        String factoryName = typeElement.getQualifiedName().toString();
                        if (this.factories.containsVal(factoryName)) {
                            continue;
                        }

                        this.log("读取到新配置 spring.factories factoryName: " + factoryName);
                        this.factories.put(autoType.getConfigureKey(), factoryName);
                    }
                }
            }
        });
    }

    /**
     * Generate factories files
     *
     * @since 1.0.0
     */
    private void generateFactoriesFiles() {
        if (this.existStartClass) {
            this.factories.put(BootAutoType.LISTENER.getConfigureKey(), APPLICATION_PID_FILE_WRITER);
        }

        if (this.factories.isEmpty()) {
            return;
        }

        Filer filer = this.processingEnv.getFiler();
        try {

            FileObject resource = filer.getResource(StandardLocation.CLASS_OUTPUT, "", FACTORIES_RESOURCE_LOCATION);
            File file = new File(resource.toUri().getPath());
            // 如果存在自定义配置, 则替换注解处理器收集到的 factories todo-dong4j : (2020-06-2 19:31) [如果存在则与注解处理器收集到的 factories 合并]
            if (file.exists()) {
                this.log("存在自定义 spring.factories 文件, 忽略自动生成");
                return;
            }
            // 写入 spring.factories
            FileObject factoriesFile = filer.createResource(StandardLocation.CLASS_OUTPUT, "", FACTORIES_RESOURCE_LOCATION);
            ProcessorUtils.writeFactoriesFile(this.factories, factoriesFile.openOutputStream());
        } catch (IOException e) {
            this.fatalError(e);
        }
    }
}
