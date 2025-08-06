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

package dev.dong4j.zeka.processor.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Set;
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

/**
 * 抽象注解处理器基类
 *
 * <p>该类为所有注解处理器提供基础功能和通用工具方法，继承自 {@link AbstractProcessor}。
 * 主要功能包括注解识别、元素类型判断、组合注解支持、错误处理和日志记录等。</p>
 *
 * <p><strong>核心功能：</strong></p>
 * <ul>
 *   <li>支持组合注解的递归解析</li>
 *   <li>提供元素类型判断的便捷方法</li>
 *   <li>统一的错误处理和日志机制</li>
 *   <li>注解镜像获取和比较</li>
 * </ul>
 *
 * <p><strong>设计特点：</strong></p>
 * <ul>
 *   <li>异常安全：所有异常都会被捕获并转换为编译错误</li>
 *   <li>调试友好：支持调试模式的详细日志输出</li>
 *   <li>扩展性强：子类只需实现 {@link #processImpl} 方法</li>
 * </ul>
 *
 * @author L.cm
 * @since 2.0.0
 */
public abstract class AbstractMicaProcessor extends AbstractProcessor {

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * 注解处理器的主入口方法
     *
     * <p>该方法是注解处理器的核心入口，负责异常处理和流程控制。
     * 所有的具体处理逻辑都委托给子类实现的 {@link #processImpl} 方法。</p>
     *
     * @param annotations 当前轮次中需要处理的注解类型集合
     * @param roundEnv    当前处理轮次的环境信息，包含被注解标记的元素等
     * @return {@code false} 表示这些注解还可以被后续的处理器处理，{@code true} 表示不再传递给其他处理器
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            return processImpl(annotations, roundEnv);
        } catch (Exception e) {
            fatalError(e);
            return false;
        }
    }

    /**
     * 具体的注解处理逻辑实现方法
     *
     * <p>子类必须实现此方法来定义具体的注解处理逻辑。该方法会在 {@link #process} 方法中被调用，
     * 所有异常都会被父类捕获并处理。</p>
     *
     * @param annotations 当前轮次中需要处理的注解类型集合
     * @param roundEnv    当前处理轮次的环境信息
     * @return 处理结果，通常返回 {@code false}
     */
    protected abstract boolean processImpl(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv);

    /**
     * 判断程序元素是否为类
     *
     * @param e 要检查的程序元素
     * @return 如果是类返回 {@code true}，否则返回 {@code false}
     */
    protected boolean isClass(Element e) {
        ElementKind kind = e.getKind();
        return kind == ElementKind.CLASS;
    }

    /**
     * 判断程序元素是否为类或接口
     *
     * @param e 要检查的程序元素
     * @return 如果是类或接口返回 {@code true}，否则返回 {@code false}
     */
    protected boolean isClassOrInterface(Element e) {
        ElementKind kind = e.getKind();
        return kind == ElementKind.CLASS || kind == ElementKind.INTERFACE;
    }

    /**
     * 获取指定元素上的注解镜像，支持组合注解
     *
     * <p>该方法不仅能找到直接标注的注解，还能递归查找组合注解（元注解）。
     * 这使得自定义注解可以组合使用，提供更好的扩展性。</p>
     *
     * @param elementUtils       元素工具类，用于获取注解信息
     * @param e                  要检查的程序元素
     * @param annotationFullName 要查找的注解全限定名
     * @return 找到的注解镜像，如果未找到则返回 {@code null}
     */
    protected AnnotationMirror getAnnotation(Elements elementUtils, Element e, String annotationFullName) {
        List<? extends AnnotationMirror> annotationList = elementUtils.getAllAnnotationMirrors(e);
        for (AnnotationMirror annotation : annotationList) {
            // 如果是对的注解
            if (isAnnotation(annotationFullName, annotation)) {
                return annotation;
            }
            // 处理组合注解
            Element element = annotation.getAnnotationType().asElement();
            String elementStr = element.toString();
            // 如果是 lombok 的注解, java 或 kotlin 元注解，继续循环
            if (elementStr.startsWith("lombok") || elementStr.startsWith("java.lang") || elementStr.startsWith("kotlin.")) {
                continue;
            }
            // 递归处理 组合注解
            return getAnnotation(elementUtils, element, annotationFullName);
        }
        return null;
    }

    /**
     * 判断指定元素是否被特定注解标记，支持组合注解
     *
     * <p>该方法是 {@link #getAnnotation} 方法的简化版本，只返回布尔值结果。
     * 同样支持组合注解的递归查找。</p>
     *
     * @param elementUtils       元素工具类
     * @param e                  要检查的程序元素
     * @param annotationFullName 要查找的注解全限定名
     * @return 如果找到匹配的注解则返回 {@code true}，否则返回 {@code false}
     */
    protected boolean isAnnotation(Elements elementUtils, Element e, String annotationFullName) {
        List<? extends AnnotationMirror> annotationList = elementUtils.getAllAnnotationMirrors(e);
        for (AnnotationMirror annotation : annotationList) {
            // 如果是对于的注解
            if (isAnnotation(annotationFullName, annotation)) {
                return true;
            }
            // 处理组合注解
            Element element = annotation.getAnnotationType().asElement();
            String elementStr = element.toString();
            // 如果是 java 元注解，继续循环
            if (elementStr.startsWith("java.lang") || elementStr.startsWith("kotlin.")) {
                continue;
            }
            // 递归处理 组合注解
            if (isAnnotation(elementUtils, element, annotationFullName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断注解镜像是否与指定的注解类型匹配
     *
     * @param annotationFullName 注解的全限定名
     * @param annotation         要比较的注解镜像
     * @return 如果匹配返回 {@code true}，否则返回 {@code false}
     */
    protected boolean isAnnotation(String annotationFullName, AnnotationMirror annotation) {
        return annotationFullName.equals(annotation.getAnnotationType().toString());
    }

    /**
     * 获取程序元素的全限定名
     *
     * @param element 程序元素
     * @return 元素的全限定名字符串
     */
    protected String getQualifiedName(Element element) {
        if (element instanceof QualifiedNameable) {
            return ((QualifiedNameable) element).getQualifiedName().toString();
        }
        return element.toString();
    }

    /**
     * 输出调试日志信息
     *
     * <p>只有在编译选项中包含 "debug" 参数时才会输出日志。
     * 可通过 Maven 编译器插件的 compilerArgs 配置启用。</p>
     *
     * @param msg 要输出的日志消息
     */
    protected void log(String msg) {
        if (processingEnv.getOptions().containsKey("debug")) {
            processingEnv.getMessager().printMessage(Kind.NOTE, msg);
        }
    }

    /**
     * 输出错误信息
     *
     * @param msg     错误消息
     * @param element 相关的程序元素
     */
    protected void error(String msg, Element element) {
        processingEnv.getMessager().printMessage(Kind.ERROR, msg, element);
    }

    /**
     * 输出带注解信息的错误
     *
     * @param msg        错误消息
     * @param element    相关的程序元素
     * @param annotation 相关的注解镜像
     */
    protected void error(String msg, Element element, AnnotationMirror annotation) {
        processingEnv.getMessager().printMessage(Kind.ERROR, msg, element, annotation);
    }

    /**
     * 输出致命错误（基于异常）
     *
     * <p>将异常堆栈信息转换为错误消息输出，防止异常传播到编译器。</p>
     *
     * @param e 发生的异常
     */
    protected void fatalError(Exception e) {
        // 不允许任何类型的异常传播到编译器
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        fatalError(writer.toString());
    }

    /**
     * 输出致命错误消息
     *
     * @param msg 错误消息
     */
    protected void fatalError(String msg) {
        processingEnv.getMessager().printMessage(Kind.ERROR, "FATAL ERROR: " + msg);
    }

}
