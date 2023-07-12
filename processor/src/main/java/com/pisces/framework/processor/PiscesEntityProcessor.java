package com.pisces.framework.processor;

import com.google.auto.service.AutoService;
import com.pisces.framework.type.annotation.TableMeta;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.function.Consumer;

/**
 * javac调用注解处理器时是使用spi机制调用
 * 因此需要在META-INF下创建spi文件
 * 使用该@AutoService注解可以自动创建
 * Google的工具
 *
 * @author jason
 * @date 2023/07/02
 */
@SupportedAnnotationTypes("com.pisces.framework.type.annotation.TableMeta")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class PiscesEntityProcessor extends AbstractProcessor {
    private Filer filer;
    private Elements elementUtils;
    private Types typeUtils;

    /**
     * 初始化，此处可以获取各种官方提供的工具类
     *
     * @param processingEnv 处理env
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.filer = processingEnv.getFiler();
        this.elementUtils = processingEnv.getElementUtils();
        this.typeUtils = processingEnv.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return false;
        }
        System.out.println("mybatis flex processor run start...");
        roundEnv.getElementsAnnotatedWith(TableMeta.class).forEach((Consumer<Element>) beanClassElement -> {
            if (beanClassElement.getSimpleName().toString().equals("ProductionLine")) {
                int num = 0;
            }
            String beanClassName = beanClassElement.toString();
            StringBuilder guessPackage = new StringBuilder();
            if (!beanClassName.contains(".")) {
                guessPackage.append("table");// = "table";
            } else {
                guessPackage.append(beanClassName, 0, beanClassName.lastIndexOf(".")).append(".table");
            }
            String tableName = camelToUnderline(beanClassElement.getSimpleName().toString());
            Map<String, Element> propertyAndColumns = new LinkedHashMap<>();
            List<String> defaultColumns = new ArrayList<>();
            TypeElement classElement = (TypeElement) beanClassElement;
            do {
                fillPropertyAndColumns(propertyAndColumns, defaultColumns, classElement);
                classElement = (TypeElement) typeUtils.asElement(classElement.getSuperclass());
            } while (classElement != null);

            genTablesClass(guessPackage.toString(), beanClassElement.getSimpleName().toString(), tableName, propertyAndColumns);
        });
        return false;
    }

    private void genTablesClass(String packageName, String entityClass, String tableName, Map<String, Element> propertyAndColumns) {
        StringBuilder queryColumns = new StringBuilder();
        propertyAndColumns.forEach((property, typeMirror) ->
                queryColumns.append(ColumnsTemplate.get(typeMirror, typeUtils)
                        .replace("@property", property)
                        .replace("@entityClass", entityClass)
                ));
        String genContent = ClassTableTemplate.TEMPLATE.replace("@package", packageName)
                .replace("@entityClass", entityClass)
                .replace("@queryColumns", queryColumns);

        try (Writer writer = filer.createSourceFile(packageName + ".Q" + entityClass).openWriter()) {
            writer.write(genContent);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String camelToUnderline(String string) {
        if (string == null || string.trim().length() == 0) {
            return "";
        }
        int len = string.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = string.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                sb.append('_');
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }

    private void fillPropertyAndColumns(Map<String, Element> propertyAndColumns, List<String> defaultColumns, TypeElement classElement) {
        for (Element fieldElement : classElement.getEnclosedElements()) {
            //all fields
            if (ElementKind.FIELD == fieldElement.getKind()) {
                Set<Modifier> modifiers = fieldElement.getModifiers();
                if (modifiers.contains(Modifier.STATIC)) {
                    //ignore static fields
                    continue;
                }

                propertyAndColumns.put(fieldElement.toString(), fieldElement);
            }
        }
    }
}
