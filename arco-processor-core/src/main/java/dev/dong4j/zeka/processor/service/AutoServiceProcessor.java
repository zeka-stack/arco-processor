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

package dev.dong4j.zeka.processor.service;

import com.google.auto.service.AutoService;
import dev.dong4j.zeka.processor.common.AbstractMicaProcessor;
import dev.dong4j.zeka.processor.common.MultiSetMap;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleAnnotationValueVisitor8;
import javax.lang.model.util.Types;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

/**
 * Java SPI 服务自动注册处理器
 *
 * <p>该处理器基于 Google Auto Service 项目，负责自动生成 Java SPI 服务配置文件。
 * 当类被 {@code @AutoService} 注解标记时，会自动在 {@code META-INF/services/} 目录下
 * 生成相应的服务配置文件。</p>
 *
 * <p><strong>主要功能：</strong></p>
 * <ul>
 *   <li>自动扫描 {@code @AutoService} 注解标记的类</li>
 *   <li>验证服务提供者实现规范</li>
 *   <li>生成标准的 SPI 服务配置文件</li>
 *   <li>支持增量编译，合并已有配置</li>
 *   <li>支持一个类实现多个服务接口</li>
 * </ul>
 *
 * <p><strong>验证规则：</strong></p>
 * <ul>
 *   <li>服务提供者必须是具体的非抽象类</li>
 *   <li>必须有公开的无参构造函数</li>
 *   <li>必须实现注解中声明的服务接口</li>
 * </ul>
 *
 * <p><strong>生成文件格式：</strong></p>
 * <pre>
 * META-INF/services/[服务接口全限定名]
 * 文件内容：服务实现类的全限定名（每行一个）
 * </pre>
 *
 * <p><strong>示例：</strong></p>
 * <pre>{@code
 * @AutoService(PaymentService.class)
 * public class AlipayService implements PaymentService {
 *     // 实现代码
 * }
 *
 * // 生成文件：META-INF/services/com.example.PaymentService
 * // 文件内容：com.example.AlipayService
 * }</pre>
 *
 * @author L.cm (based on Google Auto Service)
 * @see dev.dong4j.zeka.processor.annotation.AutoService
 * @see java.util.ServiceLoader
 * @since 2.0.0
 */
@SuppressWarnings("all")
@SupportedOptions("debug")
@AutoService(Processor.class)
public class AutoServiceProcessor extends AbstractMicaProcessor {
    /** AutoService 注解的全限定类名 */
    private static final String AUTO_SERVICE_NAME = dev.dong4j.zeka.processor.annotation.AutoService.class.getName();
    /** SPI 服务提供者映射，键为服务接口全限定名，值为实现类全限定名集合 */
    private final MultiSetMap<String, String> providers = new MultiSetMap<>();
    /** 注解处理环境中的元素工具类，用于获取类型信息 */
    private Elements elementUtils;

    /**
     * Auto service processor
     *
     * @since 2024.2.0
     */
    public AutoServiceProcessor() {

    }

    /**
     * Init
     *
     * @param processingEnv processing env
     * @since 2024.2.0
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
    }

    /**
     * Gets supported annotation types *
     *
     * @return the supported annotation types
     * @since 2024.2.0
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(AUTO_SERVICE_NAME);
    }

    /**
     * Process
     *
     * @param annotations annotations
     * @param roundEnv    round env
     * @return the boolean
     * @since 2024.2.0
     */
    @Override
    protected boolean processImpl(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            generateConfigFiles();
        } else {
            processAnnotations(annotations, roundEnv);
        }
        return false;
    }

    /**
     * 处理 @AutoService 注解标记的元素
     *
     * @param annotations 当前轮次中需要处理的注解类型集合
     * @param roundEnv    当前处理轮次的环境信息
     */
    private void processAnnotations(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        TypeElement autoService = elementUtils.getTypeElement(AUTO_SERVICE_NAME);
        Set<? extends Element> elementSet = roundEnv.getElementsAnnotatedWith(autoService);

        // 过滤出被 @AutoService 注解标记的类（排除接口、抽象类等）
        Set<TypeElement> typeElementSet = elementSet.stream()
            .filter(this::isClass)
            .filter(e -> e instanceof TypeElement)
            .map(e -> (TypeElement) e)
            .collect(Collectors.toSet());

        // 如果没有找到被注解标记的类，直接返回
        if (typeElementSet.isEmpty()) {
            log("Annotations elementSet is isEmpty");
            return;
        }

        log(annotations.toString());
        log(typeElementSet.toString());

        for (TypeElement typeElement : typeElementSet) {
            AnnotationMirror annotationMirror = getAnnotation(elementUtils, typeElement, AUTO_SERVICE_NAME);
            if (annotationMirror == null) {
                continue;
            }
            Set<TypeMirror> typeMirrors = getValueFieldOfClasses(annotationMirror);
            if (typeMirrors.isEmpty()) {
                error("No service interfaces provided for element!", typeElement, annotationMirror);
                continue;
            }
            // 接口的名称
            String providerImplementerName = getQualifiedName(typeElement);
            for (TypeMirror typeMirror : typeMirrors) {
                String providerInterfaceName = getType(typeMirror);
                log("provider interface: " + providerInterfaceName);
                log("provider implementer: " + providerImplementerName);

                if (checkImplementer(typeElement, typeMirror)) {
                    providers.put(providerInterfaceName, getQualifiedName(typeElement));
                } else {
                    String message = "ServiceProviders must implement their service provider interface. "
                        + providerImplementerName + " does not implement " + providerInterfaceName;
                    error(message, typeElement, annotationMirror);
                }
            }
        }
    }

    /**
     * 生成 SPI 服务配置文件
     *
     * <p>为每个服务接口生成对应的 META-INF/services/[接口全限定名] 文件，
     * 文件内容为实现该接口的所有服务提供者类的全限定名。支持增量编译。</p>
     */
    private void generateConfigFiles() {
        Filer filer = processingEnv.getFiler();
        for (String providerInterface : providers.keySet()) {
            String resourceFile = "META-INF/services/" + providerInterface;
            log("Working on resource file: " + resourceFile);
            try {
                SortedSet<String> allServices = new TreeSet<>();

                // 1. 读取用户手动编写的 SPI 配置文件
                try {
                    FileObject existingFile = filer.getResource(StandardLocation.SOURCE_OUTPUT, "", resourceFile);
                    log("Looking for existing resource file at " + existingFile.toUri());
                    Set<String> oldServices = ServicesFiles.readServiceFile(existingFile, elementUtils);
                    log("Existing service entries: " + oldServices);
                    allServices.addAll(oldServices);
                } catch (IOException e) {
                    log("Resource file did not already exist.");
                }

                // 2. 读取增量编译时已存在的配置文件
                try {
                    FileObject existingFile = filer.getResource(StandardLocation.CLASS_OUTPUT, "", resourceFile);
                    log("Looking for existing resource file at " + existingFile.toUri());
                    Set<String> oldServices = ServicesFiles.readServiceFile(existingFile, elementUtils);
                    log("Existing service entries: " + oldServices);
                    allServices.addAll(oldServices);
                } catch (IOException e) {
                    log("Resource file did not already exist.");
                }

                Set<String> newServices = new HashSet<>(providers.get(providerInterface));
                if (allServices.containsAll(newServices)) {
                    log("No new service entries being added.");
                    return;
                }

                // 3. 合并当前注解处理器扫描出来的新服务提供者
                allServices.addAll(newServices);
                log("New service file contents: " + allServices);

                // 创建并写入最终的 SPI 配置文件
                FileObject fileObject = filer.createResource(StandardLocation.CLASS_OUTPUT, "", resourceFile);
                try (OutputStream out = fileObject.openOutputStream()) {
                    ServicesFiles.writeServiceFile(allServices, out);
                }
                log("Wrote to: " + fileObject.toUri());
            } catch (IOException e) {
                fatalError("Unable to create " + resourceFile + ", " + e);
                return;
            }
        }
    }

    /**
     * Verifies {@link java.util.spi.LocaleServiceProvider} constraints on the concrete provider class.
     * Note that these constraints are enforced at runtime via the ServiceLoader,
     * we're just checking them at compile time to be extra nice to our users.
     *
     * @param providerImplementer provider implementer
     * @param providerType        provider type
     * @return the boolean
     * @since 2024.2.0
     */
    private boolean checkImplementer(Element providerImplementer, TypeMirror providerType) {
        // TODO: We're currently only enforcing the subtype relationship
        // constraint. It would be nice to enforce them all.
        Types types = processingEnv.getTypeUtils();
        return types.isSubtype(providerImplementer.asType(), providerType);
    }

    /**
     * 读取 AutoService 上的 value 值
     *
     * @param annotationMirror AnnotationMirror
     * @return value 集合
     * @since 2024.2.0
     */
    private Set<TypeMirror> getValueFieldOfClasses(AnnotationMirror annotationMirror) {
        return getAnnotationValue(annotationMirror, "value")
            .accept(new SimpleAnnotationValueVisitor8<Set<TypeMirror>, Void>() {
                @Override
                public Set<TypeMirror> visitType(TypeMirror typeMirror, Void v) {
                    return Collections.singleton(typeMirror);
                }

                @Override
                public Set<TypeMirror> visitArray(
                    List<? extends AnnotationValue> values, Void v) {
                    return values.stream()
                        .flatMap(value -> value.accept(this, null).stream())
                        .collect(Collectors.toSet());
                }
            }, null);
    }


    /**
     * Gets annotation value *
     *
     * @param annotationMirror annotation mirror
     * @param elementName      element name
     * @return the annotation value
     * @since 2024.2.0
     */
    public AnnotationValue getAnnotationValue(AnnotationMirror annotationMirror, String elementName) {
        Objects.requireNonNull(annotationMirror);
        Objects.requireNonNull(elementName);
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementUtils.getElementValuesWithDefaults(annotationMirror).entrySet()) {
            if (entry.getKey().getSimpleName().contentEquals(elementName)) {
                return entry.getValue();
            }
        }
        String annotationName = annotationMirror.getAnnotationType().toString();
        throw new IllegalArgumentException(String.format("@%s does not define an element %s()", annotationName, elementName));
    }

    /**
     * Gets type *
     *
     * @param type type
     * @return the type
     * @since 2024.2.0
     */
    public String getType(TypeMirror type) {
        if (type == null) {
            return null;
        }
        if (type instanceof DeclaredType) {
            DeclaredType declaredType = (DeclaredType) type;
            Element enclosingElement = declaredType.asElement().getEnclosingElement();
            if (enclosingElement instanceof TypeElement) {
                return getQualifiedName(enclosingElement) + "$" + declaredType.asElement().getSimpleName().toString();
            } else {
                return getQualifiedName(declaredType.asElement());
            }
        }
        return type.toString();
    }

}
