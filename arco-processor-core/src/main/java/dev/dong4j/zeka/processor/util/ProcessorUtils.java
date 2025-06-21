package dev.dong4j.zeka.processor.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringJoiner;
import java.util.TreeSet;
import java.util.function.Consumer;

/**
 * <p>Description: spring boot 自动化配置工具类 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:29
 * @since 1.0.0
 */
@UtilityClass
@SuppressWarnings("all")
public class ProcessorUtils {
    /** UTF_8 */
    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    /**
     * Log.
     *
     * @param msg the msg
     * @since 1.0.0
     */
    private static void log(String msg) {
        System.out.println(msg);
    }

    /**
     * 写出 spring.factories 文件
     *
     * @param factories factories 信息
     * @param output    输出流
     * @throws IOException 异常信息
     * @since 1.0.0
     */
    public static void writeFactoriesFile(@NotNull MultiSetMap<String, String> factories,
                                          OutputStream output) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, UTF_8));
        Set<String> keySet = factories.keySet();
        for (String key : keySet) {
            Set<String> values = factories.get(key);
            if (values == null || values.isEmpty()) {
                continue;
            }
            writer.write(key);
            writer.write("=\\\n  ");
            StringJoiner joiner = new StringJoiner(",\\\n  ");
            for (String value : values) {
                joiner.add(value);
            }
            writer.write(joiner.toString());
            writer.newLine();
        }
        writer.flush();
        output.close();
    }

    /**
     * 写出 spring-devtools.properties
     *
     * @param projectName 项目名
     * @param output      输出流
     * @throws IOException 异常信息
     * @since 1.0.0
     */
    public static void writeDevToolsFile(String projectName,
                                         OutputStream output) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, UTF_8));
        String format = "restart.include.%s=/%s[\\\\w-]+\\.jar";
        writer.write(String.format(format, projectName, projectName));
        writer.flush();
        output.close();
    }

    /**
     * 判断是否存在某个 class
     *
     * @param name the name
     * @return the boolean
     * @since 1.0.0
     */
    public static boolean isPresent(String name) {
        try {
            Thread.currentThread().getContextClassLoader().loadClass(name);
            return true;
        } catch (ClassNotFoundException ignored) {
        }
        return false;
    }

    /**
     * Reads the set of service classes from a service file.
     *
     * @param input not {@code null}. Closed after use.
     * @return a not {@code null Set} of service class names.
     * @throws IOException io exception
     * @since 1.0.0
     */
    public static Set<String> readServiceFile(InputStream input) throws IOException {
        HashSet<String> serviceClasses = new HashSet<>();
        try (
            InputStreamReader isr = new InputStreamReader(input, UTF_8);
            BufferedReader r = new BufferedReader(isr)
        ) {
            String line;
            while ((line = r.readLine()) != null) {
                int commentStart = line.indexOf('#');
                if (commentStart >= 0) {
                    line = line.substring(0, commentStart);
                }
                line = line.trim();
                if (!line.isEmpty()) {
                    serviceClasses.add(line);
                }
            }
            return serviceClasses;
        }
    }

    /**
     * Writes the set of service class names to a service file.
     *
     * @param services a not {@code null Collection} of service class names.
     * @param output   not {@code null}. Not closed after use.
     * @throws IOException io exception
     * @since 1.0.0
     */
    public static void writeServiceFile(@NotNull Collection<String> services, OutputStream output) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, UTF_8));
        for (String service : services) {
            writer.write(service);
            writer.newLine();
        }
        writer.flush();
    }

    /**
     * Gets all services *
     *
     * @param filer        filer
     * @param resourceFile resource file
     * @param logFunc      log func
     * @return the all services
     * @since 1.8.0
     */
    public static SortedSet<String> getAllServices(Filer filer, String resourceFile, Consumer<String> logFunc) {
        SortedSet<String> allServices = new TreeSet<>();
        try {
            FileObject existingFile = filer.getResource(StandardLocation.CLASS_OUTPUT, "", resourceFile);
            logFunc.accept("Looking for existing resource file at " + existingFile.toUri());
            Set<String> oldServices = ProcessorUtils.readServiceFile(existingFile.openInputStream());
            logFunc.accept("Existing service entries: " + oldServices);
            allServices.addAll(oldServices);
        } catch (IOException e) {
            logFunc.accept("Resource file did not already exist.");
        }
        return allServices;
    }

}
