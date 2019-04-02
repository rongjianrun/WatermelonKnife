package com.rjr.annotationprocessor;

import com.google.auto.service.AutoService;
import com.rjr.watermelonannotation.BindView;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.JavaFileObject;

/**
 * 拿到我们自定义的注解，帮我们生成相关代码（findViewById）
 */
@AutoService(Processor.class)
public class WatermelonProcessor extends AbstractProcessor {

    // 生成Java文件的对象
    private Filer filer;

    /**
     * 注解处理器要处理的事情，生成Java文件
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        /*
         * public class ClassName {     TypeElement
         * private int i;               VariableElement
         * private void method()        ExecutableElement
         */
        // 获得所有使用了BindView注解的成员变量，也就是控件
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(BindView.class);
        // 创建一个装控件的节点容器，结构化所有控件
        Map<String, List<VariableElement>> map = new HashMap<>();
        for (Element element : elements) {
            VariableElement ve = (VariableElement) element;
            String activityName = getActivityName(ve);
            List<VariableElement> list = map.get(activityName);
            if (list == null) {
                list = new ArrayList<>();
                map.put(activityName, list);
            }
            list.add(ve);
        }
        // 开始生成文件
        // 遍历集合拿到类名
        for (String activityName : map.keySet()) {
            List<VariableElement> list = map.get(activityName);
            String newActivityName = activityName + "$$ViewBinder";
            String packageName = getPackageName(list.get(0));
            // 开始写文件
            Writer writer = null;
            try {
                JavaFileObject source = filer.createSourceFile(newActivityName);
                writer = source.openWriter();
                // 开始写第一行
                writer.write("package " + packageName + ";\n\n");
                // 第二行
                writer.write("public class " + newActivityName + " implements ViewBinder<" + activityName + "> {\n\n");
                // 第三行
                writer.write("\tpublic void bind(" + activityName + " target) {\n");
                // 为控件赋值
                for (VariableElement element : list) {
                    writer.write("\t\ttarget." + element.getSimpleName() + " = target.findViewById(" + element.getAnnotation(BindView.class).value() + ");\n");
                }
                // 结束
                writer.write("\t}\n}");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }

    /**
     * 通过控件节点拿到包名
     */
    private String getPackageName(VariableElement element) {
        return processingEnv.getElementUtils().getPackageOf(element).toString();
    }

    /**
     * 通过控件节点，获取这个节点的activity name
     */
    private String getActivityName(VariableElement element) {
        // 获取上一层的节点
        TypeElement typeElement = (TypeElement) element.getEnclosingElement();
        return typeElement.getSimpleName().toString();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        // 初始化处理文件的对象
        filer = processingEnvironment.getFiler();
    }

    /**
     * 声明我们的注解处理器要处理哪些注解
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new LinkedHashSet<>();
        set.add(BindView.class.getCanonicalName());
        return set;
    }

    /**
     * 告诉JVM，我们注解处理器的版本
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return processingEnv.getSourceVersion();
    }
}
