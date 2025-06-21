package dev.dong4j.zeka.processor.handler;

import com.google.auto.service.AutoService;
import com.google.common.collect.Sets;
import dev.dong4j.zeka.processor.ArcoAbstractProcessor;
import dev.dong4j.zeka.processor.util.ProcessorUtils;
import org.jetbrains.annotations.Nullable;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.8.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.03.03 14:23
 * @since 1.8.0
 */
@SupportedOptions("debug")
@AutoService(Processor.class)
@SupportedAnnotationTypes({AutoSpiProcessor.SPI_NAME, AutoSpiProcessor.COMPONENT_NAME})
public class AutoSpiProcessor extends ArcoAbstractProcessor {

    /** 生成主目录位置 */
    static final String RESOURCE_LOCATION = "META-INF/spiservices/";
    /** 注解 @SPI */
    static final String SPI_NAME = "dev.dong4j.zeka.starter.spi.extension.SPI";
    /** 注解 @SpiService */
    static final String COMPONENT_NAME = "dev.dong4j.zeka.starter.spi.extension.SPIService";
    /** 结构化 Map 存储编排后的数据 */
    private final Map<Element, List<Element>> interfaceAndSubClazz = new ConcurrentHashMap<>();
    /** roundEnv */
    private RoundEnvironment roundEnv;

    /**
     * Process
     *
     * @param annotations annotations
     * @param roundEnv    round env
     * @return the boolean
     * @since 1.8.0
     */
    @Override
    protected boolean processImpl(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        this.roundEnv = roundEnv;
        if (roundEnv.processingOver()) {
            // 输出文件
            this.writeFile();
        } else {
            // 处理注解
            this.aggregateMetadata(annotations);
        }
        return true;
    }


    /**
     * 聚合元数据
     *
     * @param types types
     * @since 1.8.0
     */
    private void aggregateMetadata(Set<? extends TypeElement> types) {
        // 集合不可能为null，请放心
        if (types.isEmpty()) {
            return;
        }
        this.processIfPresent(types, SPI_NAME, elementsAnnotatedWith -> {
            for (Element element : elementsAnnotatedWith) {
                this.interfaceAndSubClazz.put(element, new ArrayList<>());
            }
            // 获取 实现类
            this.processIfPresent(types, COMPONENT_NAME, componentElement -> {
                Set<Element> superElements = this.interfaceAndSubClazz.keySet();
                for (Element superElement : superElements) {
                    for (Element component : componentElement) {
                        // 处理父类是泛型
                        TypeMirror erasure = this.processingEnv.getTypeUtils().erasure(superElement.asType());
                        this.addSubClazz(superElement, component, erasure);
                    }
                }
            });
        });
    }

    /**
     * Add sub clazz
     *
     * @param superElement super element
     * @param component    component
     * @param erasure      erasure field
     * @since 1.8.0
     */
    private void addSubClazz(Element superElement, Element component, TypeMirror erasure) {
        if (this.processingEnv.getTypeUtils().isSubtype(component.asType(), erasure)) {
            this.interfaceAndSubClazz.get(superElement).add(component);
        }
    }

    /**
     * lambda 入 判断后执行逻辑
     *
     * @param types          types
     * @param annotationName annotation name
     * @param func           func
     * @since 1.8.0
     */
    private void processIfPresent(Set<? extends TypeElement> types,
                                  String annotationName,
                                  Consumer<Set<? extends Element>> func) {
        Optional<? extends TypeElement> typeElement = types.stream()
            .filter(type -> type.getQualifiedName().contentEquals(annotationName))
            .findFirst();

        if (typeElement.isPresent()) {
            Set<? extends Element> elementsAnnotatedWith = this.roundEnv.getElementsAnnotatedWith(typeElement.get());
            func.accept(elementsAnnotatedWith);
        }
    }

    /**
     * Write file
     *
     * @since 1.8.0
     */
    private void writeFile() {
        Filer filer = this.processingEnv.getFiler();
        // IO 写入，不同key写进不同文件，所以异步加快
        this.interfaceAndSubClazz.entrySet().parallelStream().forEach(entry -> {
            String resourceFile = RESOURCE_LOCATION + this.getQualifiedName(entry.getKey());
            this.log("Working on resource file: " + resourceFile);
            try {
                SortedSet<String> allServices = ProcessorUtils.getAllServices(filer, resourceFile, this::log);
                Set<String> newServices = this.buildServices(entry.getValue());
                if (allServices.containsAll(newServices)) {
                    this.log("No new service entries being added.");
                    return;
                }
                allServices.addAll(newServices);
                this.log("New service file contents: " + allServices);
                this.writeFile(filer, resourceFile, allServices, StandardLocation.CLASS_OUTPUT);

            } catch (IOException e) {
                this.fatalError("Unable to create " + resourceFile + ", " + e);
            }
        });
        this.interfaceAndSubClazz.clear();
    }

    /**
     * Write file
     *
     * @param filer        filer
     * @param resourceFile resource file
     * @param allServices  all services
     * @param locations    locations
     * @throws IOException io exception
     * @since 1.8.0
     */
    private void writeFile(Filer filer, String resourceFile, SortedSet<String> allServices, StandardLocation... locations)
        throws IOException {

        for (StandardLocation location : locations) {
            FileObject sourceOut = filer.createResource(location, "", resourceFile);
            OutputStream out = sourceOut.openOutputStream();
            ProcessorUtils.writeServiceFile(allServices, out);
            out.close();
            this.log("Wrote to: " + sourceOut.toUri());
        }

    }

    /**
     * 组装 key=service lines
     *
     * @param subElements sub elements
     * @return the set
     * @since 1.8.0
     */
    private Set<String> buildServices(List<Element> subElements) {
        Set<String> eq = Sets.newHashSet();
        for (Element subElement : subElements) {
            List<? extends AnnotationMirror> annotationMirrors = subElement.getAnnotationMirrors();
            Optional<? extends AnnotationMirror> mirror =
                annotationMirrors.stream().filter(m -> m.getAnnotationType().toString().equals(COMPONENT_NAME)).findFirst();
            if (mirror.isPresent()) {
                AnnotationMirror annotationMirror = mirror.get();
                eq.add(String.format("%s=%s", this.getValue(subElement, annotationMirror), this.getQualifiedName(subElement)));
            }
        }
        return eq;
    }

    /**
     * 读取 SPI Component 上的 value 值
     *
     * @param element          element
     * @param annotationMirror AnnotationMirror
     * @return value 集合
     * @since 1.0.0
     */
    private String getValue(Element element, AnnotationMirror annotationMirror) {
        AnnotationValue value = this.getAnnotationValue(annotationMirror, "value");
        String val = value.toString();
        if (isBlank(val) || "\"\"".equals(val)) {
            String camel = element.getSimpleName().toString();
            return camel.substring(0, 1).toLowerCase() + camel.substring(1);
        }
        // formatter \"\"
        return val.substring(1, val.length() - 1);
    }

    /**
     * Gets annotation value *
     *
     * @param annotationMirror annotation mirror
     * @param elementName      element name
     * @return the annotation value
     * @since 1.0.0
     */
    private AnnotationValue getAnnotationValue(AnnotationMirror annotationMirror, String elementName) {
        Objects.requireNonNull(annotationMirror);
        Objects.requireNonNull(elementName);
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry :
            this.processingEnv.getElementUtils().getElementValuesWithDefaults(annotationMirror).entrySet()) {
            if (entry.getKey().getSimpleName().contentEquals(elementName)) {
                return entry.getValue();
            }
        }
        String annotationName = annotationMirror.getAnnotationType().toString();
        throw new IllegalArgumentException(String.format("@%s does not define an element %s()", annotationName, elementName));
    }

    /**
     * Is blank
     *
     * @param cs cs
     * @return the boolean
     * @since 1.0.0
     */
    private static boolean isBlank(CharSequence cs) {
        return !hasText(cs);
    }

    /**
     * Has text
     *
     * @param str str
     * @return the boolean
     * @since 1.0.0
     */
    private static boolean hasText(@Nullable CharSequence str) {
        return (str != null && str.length() > 0 && containsText(str));
    }

    /**
     * Contains text
     *
     * @param str str
     * @return the boolean
     * @since 1.0.0
     */
    private static boolean containsText(CharSequence str) {
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }
}

