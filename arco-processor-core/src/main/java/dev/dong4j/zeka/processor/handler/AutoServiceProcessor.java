package dev.dong4j.zeka.processor.handler;

import com.google.auto.service.AutoService;
import dev.dong4j.zeka.processor.ArcoAbstractProcessor;
import dev.dong4j.zeka.processor.util.MultiSetMap;
import dev.dong4j.zeka.processor.util.ProcessorUtils;
import org.jetbrains.annotations.NotNull;

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
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

/**
 * java spi 服务自动处理器 参考: google auto
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.04.22 18:00
 * @since 1.0.0
 */
@SupportedOptions("debug")
@AutoService(Processor.class)
public class AutoServiceProcessor extends ArcoAbstractProcessor {
    /**
     * AutoService 注解名
     *
     * @see dev.dong4j.zeka.processor.annotation.AutoService
     */
    private static final String AUTO_SERVICE_NAME = "dev.dong4j.zeka.processor.annotation.AutoService";
    /** spi 服务集合, key 接口 -> value 实现列表 */
    private final MultiSetMap<String, String> providers = new MultiSetMap<>();
    /** 元素辅助类 */
    private Elements elementUtils;

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
     * Gets supported annotation types *
     *
     * @return the supported annotation types
     * @since 1.0.0
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(AUTO_SERVICE_NAME);
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
            this.generateConfigFiles();
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
    private void processAnnotations(Set<? extends TypeElement> annotations, @NotNull RoundEnvironment roundEnv) {
        TypeElement autoService = this.elementUtils.getTypeElement(AUTO_SERVICE_NAME);
        Set<? extends Element> elementSet = roundEnv.getElementsAnnotatedWith(autoService);
        // 过滤 TypeElement
        Set<TypeElement> typeElementSet = elementSet.stream()
            .filter(this::isClass)
            .filter(TypeElement.class::isInstance)
            .map(TypeElement.class::cast)
            .collect(Collectors.toSet());

        // 如果为空直接跳出
        if (typeElementSet.isEmpty()) {
            this.log("Annotations elementSet is isEmpty");
            return;
        }

        this.log(annotations.toString());
        this.log(typeElementSet.toString());

        // 接口的名称
        typeElementSet.forEach(typeElement -> {
            AnnotationMirror annotationMirror = this.getAnnotation(this.elementUtils, typeElement, AUTO_SERVICE_NAME);
            if (annotationMirror == null) {
                return;
            }
            Set<TypeMirror> typeMirrors = this.getValueFieldOfClasses(annotationMirror);
            if (typeMirrors.isEmpty()) {
                this.error("No service interfaces provided for element!", typeElement, annotationMirror);
                return;
            }
            String providerImplementerName = this.getQualifiedName(typeElement);
            for (TypeMirror typeMirror : typeMirrors) {
                String providerInterfaceName = this.getType(typeMirror);
                this.log("provider interface: " + providerInterfaceName);
                this.log("provider implementer: " + providerImplementerName);

                if (this.checkImplementer(typeElement, typeMirror)) {
                    this.providers.put(providerInterfaceName, this.getQualifiedName(typeElement));
                } else {
                    String message = "ServiceProviders must implement their service provider interface. "
                        + providerImplementerName + " does not implement " + providerInterfaceName;
                    this.error(message, typeElement, annotationMirror);
                }
            }
        });
    }

    /**
     * Generate config files
     *
     * @since 1.0.0
     */
    private void generateConfigFiles() {
        Filer filer = this.processingEnv.getFiler();
        for (String providerInterface : this.providers.keySet()) {
            String resourceFile = "META-INF/services/" + providerInterface;
            this.log("Working on resource file: " + resourceFile);
            try {
                SortedSet<String> allServices = ProcessorUtils.getAllServices(filer, resourceFile, this::log);

                Set<String> newServices = new HashSet<>(this.providers.get(providerInterface));
                if (allServices.containsAll(newServices)) {
                    this.log("No new service entries being added.");
                    return;
                }

                allServices.addAll(newServices);
                this.log("New service file contents: " + allServices);
                FileObject fileObject = filer.createResource(StandardLocation.CLASS_OUTPUT, "", resourceFile);
                OutputStream out = fileObject.openOutputStream();
                ProcessorUtils.writeServiceFile(allServices, out);
                out.close();
                this.log("Wrote to: " + fileObject.toUri());
            } catch (IOException e) {
                this.fatalError("Unable to create " + resourceFile + ", " + e);
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
     * @since 1.0.0
     */
    private boolean checkImplementer(@NotNull Element providerImplementer, TypeMirror providerType) {
        // TODO: We're currently only enforcing the subtype relationship
        // constraint. It would be nice to enforce them all.
        Types types = this.processingEnv.getTypeUtils();
        return types.isSubtype(providerImplementer.asType(), providerType);
    }

    /**
     * 读取 AutoService 上的 value 值
     *
     * @param annotationMirror AnnotationMirror
     * @return value 集合
     * @since 1.0.0
     */
    private Set<TypeMirror> getValueFieldOfClasses(AnnotationMirror annotationMirror) {
        return this.getAnnotationValue(annotationMirror, "value")
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
     * @since 1.0.0
     */
    public AnnotationValue getAnnotationValue(AnnotationMirror annotationMirror, String elementName) {
        Objects.requireNonNull(annotationMirror);
        Objects.requireNonNull(elementName);
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry :
            this.elementUtils.getElementValuesWithDefaults(annotationMirror).entrySet()) {
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
     * @since 1.0.0
     */
    public String getType(TypeMirror type) {
        if (type == null) {
            return null;
        }
        if (type instanceof DeclaredType) {
            DeclaredType declaredType = (DeclaredType) type;
            Element enclosingElement = declaredType.asElement().getEnclosingElement();
            if (enclosingElement instanceof TypeElement) {
                return this.getQualifiedName(enclosingElement) + "$" + declaredType.asElement().getSimpleName().toString();
            } else {
                return this.getQualifiedName(declaredType.asElement());
            }
        }
        return type.toString();
    }

}
