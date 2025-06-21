package dev.dong4j.zeka.processor;

import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic.Kind;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Set;

/**
 * <p>Description: 抽象 处理器</p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.26 21:37
 * @since 1.0.0
 */
@SuppressWarnings("all")
public abstract class ArcoAbstractProcessor extends AbstractProcessor {
    /**
     * The location to look for factories.
     * <p>Can be present in multiple JAR files.
     */
    public static final String FACTORIES_RESOURCE_LOCATION = "META-INF/spring.factories";

    /**
     * Gets supported source version *
     *
     * @return the supported source version
     * @since 1.0.0
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * AutoService 注解处理器
     *
     * @param annotations 注解 getSupportedAnnotationTypes
     * @param roundEnv    扫描到的 注解新
     * @return 是否完成 boolean
     * @since 1.0.0
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            return this.processImpl(annotations, roundEnv);
        } catch (Exception e) {
            this.fatalError(e);
            return false;
        }
    }

    /**
     * Process boolean
     *
     * @param annotations annotations
     * @param roundEnv    round env
     * @return the boolean
     * @since 1.0.0
     */
    protected abstract boolean processImpl(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv);

    /**
     * 判断为类
     *
     * @param e Element
     * @return {boolean}
     * @since 1.0.0
     */
    protected boolean isClass(@NotNull Element e) {
        ElementKind kind = e.getKind();
        return kind == ElementKind.CLASS;
    }

    /**
     * 判断为类或者接口
     *
     * @param e Element
     * @return {boolean}
     * @since 1.0.0
     */
    protected boolean isClassOrInterface(@NotNull Element e) {
        ElementKind kind = e.getKind();
        return kind == ElementKind.CLASS || kind == ElementKind.INTERFACE;
    }

    /**
     * 获取注解, 支持组合注解
     *
     * @param elementUtils       elementUtils
     * @param e                  Element
     * @param annotationFullName annotationFullName
     * @return {boolean}
     * @since 1.0.0
     */
    protected AnnotationMirror getAnnotation(@NotNull Elements elementUtils, Element e, String annotationFullName) {
        List<? extends AnnotationMirror> annotationList = elementUtils.getAllAnnotationMirrors(e);
        for (AnnotationMirror annotation : annotationList) {
            // 如果是对于的注解
            if (this.isAnnotation(annotationFullName, annotation)) {
                return annotation;
            }
            // 处理组合注解
            Element element = annotation.getAnnotationType().asElement();
            // 如果是 java 元注解, 继续循环
            if (element.toString().startsWith("java.lang") || element.toString().startsWith("lombok")) {
                continue;
            }
            // 递归处理 组合注解
            return this.getAnnotation(elementUtils, element, annotationFullName);
        }
        return null;
    }

    /**
     * 判断是相同的注解, 支持组合注解
     *
     * @param elementUtils       elementUtils
     * @param e                  Element
     * @param annotationFullName annotationFullName
     * @return {boolean}
     * @since 1.0.0
     */
    protected boolean isAnnotation(@NotNull Elements elementUtils, Element e, String annotationFullName) {
        List<? extends AnnotationMirror> annotationList = elementUtils.getAllAnnotationMirrors(e);
        for (AnnotationMirror annotation : annotationList) {
            // 如果是对于的注解
            if (this.isAnnotation(annotationFullName, annotation)) {
                return true;
            }
            // 处理组合注解
            Element element = annotation.getAnnotationType().asElement();
            // 如果是 java 元注解, 继续循环
            if (element.toString().startsWith("java.lang") || element.toString().startsWith("lombok")) {
                continue;
            }
            // 递归处理 组合注解
            if (this.isAnnotation(elementUtils, element, annotationFullName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否同一个注解
     *
     * @param annotationFullName annotationFullName
     * @param annotation         AnnotationMirror
     * @return {boolean}
     * @since 1.0.0
     */
    protected boolean isAnnotation(@NotNull String annotationFullName, @NotNull AnnotationMirror annotation) {
        return annotationFullName.equals(annotation.getAnnotationType().toString());
    }

    /**
     * 获取属性的名称
     *
     * @param element Element
     * @return {String}
     * @since 1.0.0
     */
    protected String getQualifiedName(Element element) {
        if (element instanceof QualifiedNameable) {
            return ((QualifiedNameable) element).getQualifiedName().toString();
        }
        return element.toString();
    }

    /**
     * Log *
     *
     * @param msg msg
     * @since 1.0.0
     */
    protected void log(String msg) {
        if (processingEnv.getOptions().containsKey("debug")) {
            processingEnv.getMessager().printMessage(Kind.NOTE, msg);
        }
    }

    /**
     * Error *
     *
     * @param msg     msg
     * @param element element
     * @since 1.0.0
     */
    protected void error(String msg, Element element) {
        processingEnv.getMessager().printMessage(Kind.ERROR, msg, element);
    }

    /**
     * Error *
     *
     * @param msg        msg
     * @param element    element
     * @param annotation annotation
     * @since 1.0.0
     */
    protected void error(String msg, Element element, AnnotationMirror annotation) {
        processingEnv.getMessager().printMessage(Kind.ERROR, msg, element, annotation);
    }

    /**
     * Fatal error *
     *
     * @param e e
     * @since 1.0.0
     */
    protected void fatalError(@NotNull Exception e) {
        // We don't allow exceptions of any kind to propagate to the compiler
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        fatalError(writer.toString());
    }

    /**
     * Fatal error *
     *
     * @param msg msg
     * @since 1.0.0
     */
    protected void fatalError(String msg) {
        processingEnv.getMessager().printMessage(Kind.ERROR, "FATAL ERROR: " + msg);
    }

}
