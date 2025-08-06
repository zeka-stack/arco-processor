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

import com.google.auto.service.AutoService;
import dev.dong4j.zeka.processor.annotation.AotBeanFactoryInitialization;
import dev.dong4j.zeka.processor.annotation.AotBeanRegistration;
import dev.dong4j.zeka.processor.annotation.AotRuntimeHintsRegistrar;
import dev.dong4j.zeka.processor.common.AbstractMicaProcessor;
import dev.dong4j.zeka.processor.common.AotAutoType;
import dev.dong4j.zeka.processor.common.MultiSetMap;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

/**
 * Spring Boot AOT (Ahead-of-Time) 编译配置处理器
 *
 * <p>该处理器专门用于 Spring Boot 3.x 的 AOT 编译支持，负责生成 {@code META-INF/spring/aot.factories} 文件。
 * AOT 编译允许在编译时预处理 Spring 配置，提高应用启动性能和支持原生镜像编译。</p>
 *
 * <p><strong>主要功能：</strong></p>
 * <ul>
 *   <li>扫描 AOT 相关注解标记的类</li>
 *   <li>自动注册 AOT 处理器到配置文件</li>
 *   <li>支持运行时提示注册器</li>
 *   <li>支持 Bean 注册和工厂初始化处理器</li>
 *   <li>支持增量编译和配置合并</li>
 * </ul>
 *
 * <p><strong>支持的 AOT 注解：</strong></p>
 * <ul>
 *   <li>{@code @AotRuntimeHintsRegistrar} - 运行时提示注册器</li>
 *   <li>{@code @AotBeanRegistration} - Bean 注册 AOT 处理器</li>
 *   <li>{@code @AotBeanFactoryInitialization} - Bean 工厂初始化 AOT 处理器</li>
 * </ul>
 *
 * <p><strong>AOT 编译优势：</strong></p>
 * <ul>
 *   <li>提高应用启动速度</li>
 *   <li>减少运行时内存占用</li>
 *   <li>支持 GraalVM 原生镜像编译</li>
 *   <li>编译时错误检测</li>
 * </ul>
 *
 * <p><strong>生成文件：</strong><br>
 * {@code META-INF/spring/aot.factories}</p>
 *
 * @author L.cm
 * @see AotAutoType
 * @see org.springframework.aot.hint.RuntimeHintsRegistrar
 * @see org.springframework.beans.factory.aot.BeanRegistrationAotProcessor
 * @since 2.0.0
 */
@SuppressWarnings("DuplicatedCode")
@AutoService(Processor.class)
@SupportedOptions("debug")
public class AotFactoriesProcessor extends AbstractMicaProcessor {
    /** AOT 配置文件位置，可存在于多个 JAR 文件中 */
    private static final String FACTORIES_RESOURCE_LOCATION = "META-INF/spring/aot.factories";
    /** AOT 配置数据存储容器，键为配置类型，值为实现类集合 */
    private final MultiSetMap<String, String> factories = new MultiSetMap<>();
    /** 注解处理环境中的元素工具类，用于获取类型信息 */
    private Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Stream.of(
            AotRuntimeHintsRegistrar.class.getName(),
            AotBeanRegistration.class.getName(),
            AotBeanFactoryInitialization.class.getName()
        ).collect(Collectors.toSet());
    }

    @Override
    protected boolean processImpl(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            // 1. 生成 aot.factories
            generateFactoriesFiles();
        } else {
            processAnnotations(annotations, roundEnv);
        }
        return false;
    }

    /**
     * 处理扫描到的 AOT 注解元素
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
            .filter(TypeElement.class::isInstance)
            .map(TypeElement.class::cast)
            .collect(Collectors.toSet());
        // 如果没有找到任何符合条件的元素，直接返回
        if (typeElementSet.isEmpty()) {
            log("Annotations elementSet is isEmpty");
            return;
        }

        for (TypeElement typeElement : typeElementSet) {
            // 遍历所有支持的 AOT 注解类型，检查当前元素是否被标记
            for (AotAutoType autoType : AotAutoType.values()) {
                String annotation = autoType.getAnnotation();
                if (isAnnotation(elementUtils, typeElement, annotation)) {
                    log("Found @" + annotation + " Element: " + typeElement.toString());

                    String factoryName = typeElement.getQualifiedName().toString();
                    if (factories.containsVal(factoryName)) {
                        continue;
                    }

                    log("读取到新配置 aot.factories factoryName：" + factoryName);
                    factories.put(autoType.getConfigureKey(), factoryName);
                }
            }
        }
    }

    /**
     * 生成 aot.factories 配置文件
     *
     * <p>该方法将收集到的所有 AOT 配置信息合并并写入到 META-INF/spring/aot.factories 文件中。
     * 支持增量编译，会合并已有的配置文件内容。</p>
     */
    private void generateFactoriesFiles() {
        if (factories.isEmpty()) {
            return;
        }
        Filer filer = processingEnv.getFiler();
        try {
            // 用于存储所有 aot.factories 配置的容器
            MultiSetMap<String, String> allFactories = new MultiSetMap<>();

            // 1. 读取用户手动编写的 aot.factories 文件
            try {
                FileObject existingFactoriesFile = filer.getResource(StandardLocation.SOURCE_OUTPUT, "", FACTORIES_RESOURCE_LOCATION);
                log("Looking for existing aot.factories file at " + existingFactoriesFile.toUri());
                MultiSetMap<String, String> existingFactories = FactoriesFiles.readFactoriesFile(existingFactoriesFile, elementUtils);
                log("Existing aot.factories entries: " + existingFactories);
                allFactories.putAll(existingFactories);
            } catch (IOException e) {
                log("aot.factories resource file not found.");
            }

            // 2. 读取增量编译时已存在的 aot.factories 文件
            try {
                FileObject existingFactoriesFile = filer.getResource(StandardLocation.CLASS_OUTPUT, "", FACTORIES_RESOURCE_LOCATION);
                log("Looking for existing aot.factories file at " + existingFactoriesFile.toUri());
                MultiSetMap<String, String> existingFactories = FactoriesFiles.readFactoriesFile(existingFactoriesFile, elementUtils);
                log("Existing aot.factories entries: " + existingFactories);
                allFactories.putAll(existingFactories);
            } catch (IOException e) {
                log("aot.factories resource file did not already exist.");
            }

            // 3. 合并当前注解处理器扫描出来的新配置
            allFactories.putAll(factories);
            log("New aot.factories file contents: " + allFactories);

            // 创建并写入最终的 AOT 配置文件
            FileObject factoriesFile = filer.createResource(StandardLocation.CLASS_OUTPUT, "", FACTORIES_RESOURCE_LOCATION);
            try (OutputStream out = factoriesFile.openOutputStream()) {
                FactoriesFiles.writeFactoriesFile(allFactories, out);
            }
        } catch (IOException e) {
            fatalError(e);
        }
    }

}
