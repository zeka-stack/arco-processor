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

import com.google.auto.service.AutoService;
import dev.dong4j.zeka.processor.annotation.AutoIgnore;
import dev.dong4j.zeka.processor.common.AbstractMicaProcessor;
import dev.dong4j.zeka.processor.common.BootAutoType;
import dev.dong4j.zeka.processor.common.MultiSetMap;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
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

/**
 * Spring Boot 自动配置文件生成处理器
 *
 * <p>该处理器负责自动生成 Spring Boot 的自动配置相关文件，包括：</p>
 * <ul>
 *   <li>{@code META-INF/spring.factories} - 传统的 Spring Boot 2.x 自动配置文件</li>
 *   <li>{@code META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports} - Spring Boot 2.7+ 的新格式</li>
 * </ul>
 *
 * <p><strong>主要功能：</strong></p>
 * <ul>
 *   <li>扫描带有 {@code @Component} 等注解的类，自动注册到配置文件</li>
 *   <li>支持 {@code @FeignClient} 注解的自动配置处理</li>
 *   <li>处理各种 Spring Boot 扩展点的自动注册</li>
 *   <li>支持增量编译，合并已有配置</li>
 *   <li>自动检测启动类并注册相关监听器</li>
 * </ul>
 *
 * <p><strong>支持的注解类型：</strong></p>
 * <ul>
 *   <li>Spring Boot 自动配置注解（通过 {@link BootAutoType} 定义）</li>
 *   <li>Feign 客户端注解</li>
 *   <li>忽略注解 {@code @AutoIgnore}</li>
 * </ul>
 *
 * <p><strong>工作流程：</strong></p>
 * <ol>
 *   <li>在每个编译轮次中扫描带注解的类</li>
 *   <li>根据注解类型分类收集信息</li>
 *   <li>在最后一轮编译时生成配置文件</li>
 *   <li>合并用户手动配置和处理器自动发现的配置</li>
 * </ol>
 *
 * @author L.cm
 * @see BootAutoType
 * @see AutoIgnore
 * @since 1.0.0
 */
@SuppressWarnings("all")
@AutoService(Processor.class)
@SupportedAnnotationTypes("*")
@SupportedOptions("debug")
public class AutoFactoriesProcessor extends AbstractMicaProcessor {
    /** Feign 客户端注解全限定名 */
    private static final String FEIGN_CLIENT_ANNOTATION = "org.springframework.cloud.openfeign.FeignClient";
    /** Feign 自动配置类的配置键 */
    private static final String FEIGN_AUTO_CONFIGURE_KEY = "dev.dong4j.zeka.starter.feign.autoconfigure.ZekaFeignAutoConfiguration";
    /** Spring Boot 传统配置文件位置，可存在于多个 JAR 文件中 */
    private static final String FACTORIES_RESOURCE_LOCATION = "META-INF/spring.factories";
    /** DevTools 配置文件位置，包含 Configuration 注解的 jar 通常需要此配置 */
    private static final String DEVTOOLS_RESOURCE_LOCATION = "META-INF/spring-devtools.properties";
    /** Spring Boot 自动配置注解全限定名 */
    private static final String AUTO_CONFIGURATION = "org.springframework.boot.autoconfigure.AutoConfiguration";
    /** Spring Boot 2.7+ 新格式自动配置文件位置 */
    private static final String AUTO_CONFIGURATION_IMPORTS_LOCATION = "META-INF/spring/" + AUTO_CONFIGURATION + ".imports";
    /** 配置数据存储容器，键为配置类型，值为实现类集合 */
    private final MultiSetMap<String, String> factories = new MultiSetMap<>();
    /** Spring Boot 2.7+ 自动配置类集合 */
    private final Set<String> autoConfigurationImportsSet = new LinkedHashSet<>();
    /** 注解处理环境中的元素工具类 */
    private Elements elementUtils;
    /** 标记是否存在继承自 ZekaStackStarter 的启动类 */
    private boolean existStartClass = false;
    /** 应用启动类的父类全限定名 */
    private static final String START_CLASS_NAME = "dev.dong4j.zeka.starter.launcher.ZekaStackStarter";
    /** 应用 PID 文件写入器类名，用于生成 app.pid 文件 */
    private static final String APPLICATION_PID_FILE_WRITER = "org.springframework.boot.context.ApplicationPidFileWriter";

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
    }

    @Override
    protected boolean processImpl(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            // 1. 生成 spring boot 2.7.x @AutoConfiguration
            generateAutoConfigurationImportsFiles();
            // 2. 生成 spring.factories
            generateFactoriesFiles();
        } else {
            processAnnotations(annotations, roundEnv);
        }
        return false;
    }

    /**
     * 处理扫描到的注解元素
     *
     * @param annotations 当前轮次中需要处理的注解类型集合
     * @param roundEnv    当前处理轮次的环境信息
     */
    private void processAnnotations(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // 输出调试信息，可通过 gradle build --debug 查看
        log(annotations.toString());
        Set<? extends Element> elementSet = roundEnv.getRootElements();
        log("All Element set: " + elementSet.toString());

        // 过滤出类或接口类型的元素
        Set<TypeElement> typeElementSet = elementSet.stream()
            .filter(this::isClassOrInterface)
            .filter(e -> e instanceof TypeElement)
            .map(e -> (TypeElement) e)
            .collect(Collectors.toSet());
        // 如果没有找到任何符合条件的元素，直接返回
        if (typeElementSet.isEmpty()) {
            log("Annotations elementSet is isEmpty");
            return;
        }
        for (TypeElement typeElement : typeElementSet) {
            if (!this.existStartClass && typeElement.getSuperclass() != null && START_CLASS_NAME.equals(typeElement.getSuperclass().toString())) {
                this.existStartClass = true;
            }

            // ignore @AutoIgnore Element
            if (isAnnotation(elementUtils, typeElement, AutoIgnore.class.getName())) {
                log("Found @AutoIgnore annotation，ignore Element: " + typeElement.toString());
            } else if (isAnnotation(elementUtils, typeElement, FEIGN_CLIENT_ANNOTATION)) {
                log("Found @FeignClient Element: " + typeElement.toString());

                ElementKind elementKind = typeElement.getKind();
                // Feign Client 只处理 接口
                if (ElementKind.INTERFACE != elementKind) {
                    fatalError("@FeignClient Element " + typeElement + " 不是接口。");
                    continue;
                }

                String factoryName = typeElement.getQualifiedName().toString();
                if (factories.containsVal(factoryName)) {
                    continue;
                }

                log("读取到新配置 spring.factories factoryName：" + factoryName);
                factories.put(FEIGN_AUTO_CONFIGURE_KEY, factoryName);
            } else {
                // 1. 生成 2.7.x 的 spi
                if (isAnnotation(elementUtils, typeElement, BootAutoType.COMPONENT_ANNOTATION)) {
                    String autoConfigurationBeanName = typeElement.getQualifiedName().toString();
                    autoConfigurationImportsSet.add(autoConfigurationBeanName);
                    log("读取到自动配置 @AutoConfiguration：" + autoConfigurationBeanName);
                }
                // 2. 老的 spring.factories
                for (BootAutoType autoType : BootAutoType.values()) {
                    String annotation = autoType.getAnnotation();
                    if (isAnnotation(elementUtils, typeElement, annotation)) {
                        log("Found @" + annotation + " Element: " + typeElement.toString());

                        String factoryName = typeElement.getQualifiedName().toString();
                        if (factories.containsVal(factoryName)) {
                            continue;
                        }

                        log("读取到新配置 spring.factories factoryName：" + factoryName);
                        factories.put(autoType.getConfigureKey(), factoryName);
                    }
                }
            }
        }
    }

    /**
     * 生成 spring.factories 配置文件
     *
     * <p>该方法将收集到的所有配置信息合并并写入到 META-INF/spring.factories 文件中。
     * 支持增量编译，会合并已有的配置文件内容。</p>
     */
    private void generateFactoriesFiles() {
        if (factories.isEmpty()) {
            return;
        }
        Filer filer = processingEnv.getFiler();
        try {
            // 用于存储所有 spring.factories 配置的容器
            MultiSetMap<String, String> allFactories = new MultiSetMap<>();

            // 1. 读取用户手动编写的 spring.factories 文件
            try {
                FileObject existingFactoriesFile = filer.getResource(StandardLocation.SOURCE_OUTPUT, "", FACTORIES_RESOURCE_LOCATION);
                log("Looking for existing spring.factories file at " + existingFactoriesFile.toUri());
                MultiSetMap<String, String> existingFactories = FactoriesFiles.readFactoriesFile(existingFactoriesFile, elementUtils);
                log("Existing spring.factories entries: " + existingFactories);
                allFactories.putAll(existingFactories);
            } catch (IOException e) {
                log("spring.factories resource file not found.");
            }

            // 2. 读取增量编译时已存在的 spring.factories 文件
            try {
                FileObject existingFactoriesFile = filer.getResource(StandardLocation.CLASS_OUTPUT, "", FACTORIES_RESOURCE_LOCATION);
                log("Looking for existing spring.factories file at " + existingFactoriesFile.toUri());
                MultiSetMap<String, String> existingFactories = FactoriesFiles.readFactoriesFile(existingFactoriesFile, elementUtils);
                log("Existing spring.factories entries: " + existingFactories);
                allFactories.putAll(existingFactories);
            } catch (IOException e) {
                log("spring.factories resource file did not already exist.");
            }

            // 3. 合并当前注解处理器扫描出来的新配置
            allFactories.putAll(factories);
            log("New spring.factories file contents: " + allFactories);

            // 创建并写入最终的配置文件
            FileObject factoriesFile = filer.createResource(StandardLocation.CLASS_OUTPUT, "", FACTORIES_RESOURCE_LOCATION);
            try (OutputStream out = factoriesFile.openOutputStream()) {
                FactoriesFiles.writeFactoriesFile(allFactories, out);
            }

        } catch (IOException e) {
            fatalError(e);
        }
    }

    private void generateAutoConfigurationImportsFiles() {
        if (this.existStartClass) {
            this.factories.put(BootAutoType.LISTENER.getConfigureKey(), APPLICATION_PID_FILE_WRITER);
        }

        if (autoConfigurationImportsSet.isEmpty()) {
            return;
        }
        Filer filer = processingEnv.getFiler();
        try {
            // AutoConfiguration 配置
            Set<String> allAutoConfigurationImports = new LinkedHashSet<>();
            // 1. 用户手动配置项目下的 AutoConfiguration 文件
            try {
                FileObject existingFactoriesFile = filer.getResource(StandardLocation.SOURCE_OUTPUT, "", AUTO_CONFIGURATION_IMPORTS_LOCATION);
                // 查找是否已经存在 spring.factories
                log("Looking for existing AutoConfiguration imports file at " + existingFactoriesFile.toUri());
                Set<String> existingSet = FactoriesFiles.readAutoConfigurationImports(existingFactoriesFile);
                log("Existing AutoConfiguration imports entries: " + existingSet);
                allAutoConfigurationImports.addAll(existingSet);
            } catch (IOException e) {
                log("AutoConfiguration imports resource file not found.");
            }
            // 2. 增量编译，已经存在的配置文件
            try {
                FileObject existingFactoriesFile = filer.getResource(StandardLocation.CLASS_OUTPUT, "", AUTO_CONFIGURATION_IMPORTS_LOCATION);
                // 查找是否已经存在 spring.factories
                log("Looking for existing AutoConfiguration imports file at " + existingFactoriesFile.toUri());
                Set<String> existingSet = FactoriesFiles.readAutoConfigurationImports(existingFactoriesFile);
                log("Existing AutoConfiguration imports entries: " + existingSet);
                allAutoConfigurationImports.addAll(existingSet);
            } catch (IOException e) {
                log("AutoConfiguration imports resource file did not already exist.");
            }
            // 3. 处理器扫描出来的新的配置
            allAutoConfigurationImports.addAll(autoConfigurationImportsSet);
            log("New AutoConfiguration imports file contents: " + allAutoConfigurationImports);
            FileObject autoConfigurationImportsFile = filer.createResource(StandardLocation.CLASS_OUTPUT, "", AUTO_CONFIGURATION_IMPORTS_LOCATION);
            try (OutputStream out = autoConfigurationImportsFile.openOutputStream()) {
                FactoriesFiles.writeAutoConfigurationImportsFile(allAutoConfigurationImports, out);
            }
        } catch (IOException e) {
            fatalError(e);
        }
    }

}
