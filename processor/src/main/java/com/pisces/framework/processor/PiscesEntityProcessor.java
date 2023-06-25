package com.pisces.framework.processor;

import com.google.auto.service.AutoService;
import jakarta.persistence.Table;

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
 * 该注解表明当前注解处理器仅能处理
 * com.pisces.framework.core.compiler.PiscesEntity注解
 */
//@SupportedAnnotationTypes("com.pisces.framework.processor.PiscesEntity")
@SupportedAnnotationTypes("jakarta.persistence.Table")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
/**
 * javac调用注解处理器时是使用spi机制调用
 * 因此需要在META-INF下创建spi文件
 * 使用该@AutoService注解可以自动创建
 * Google的工具
 */
@AutoService(Processor.class)
public class PiscesEntityProcessor extends AbstractProcessor {
    private static final String classTableTemplate = "package @package;\n" +
            "\n" +
            "import com.pisces.framework.core.query.column.*;\n" +
            "import com.pisces.framework.core.query.TableDef;\n" +
            "\n" +
            "// Auto generate by mybatis-flex, do not modify it.\n" +
            "public class @tablesClassName {\n" +
            "@classesInfo" +
            "}\n";

    private static final String tableDefTemplate = "\n\n    public static final @entityClassTableDef @tableField = new @entityClassTableDef(\"@tableName\");\n";

    private static final String classTemplate = "\n" +
            "    public static class @entityClassTableDef extends TableDef {\n" +
            "\n" +
            "@queryColumns" +
            "\n" +
            "        public @entityClassTableDef(String tableName) {\n" +
            "            super(tableName);\n" +
            "        }\n" +
            "    }\n";

    private static final String columnsTemplate = "        public QueryColumn @property = new QueryColumn(this, \"@columnName\");\n";

    private static final String defaultColumnsTemplate = "\n        public QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{@allColumns};\n";
    private static final String allColumnsTemplate = "        public QueryColumn[] ALL_COLUMNS = new QueryColumn[]{@allColumns};\n\n";

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
        roundEnv.getElementsAnnotatedWith(Table.class).forEach((Consumer<Element>) entityClassElement -> {
            Table table = entityClassElement.getAnnotation(Table.class);
            String entityClassName = entityClassElement.toString();
            StringBuilder guessPackage = new StringBuilder();
            if (!entityClassName.contains(".")) {
                guessPackage.append("table");// = "table";
            } else {
                guessPackage.append(entityClassName.substring(0, entityClassName.lastIndexOf("."))).append(".table");
            }
            String className = "Q" + entityClassElement.getSimpleName();
            String tableName = table != null && table.name().trim().length() != 0
                    ? table.name()
                    : camelToUnderline(entityClassElement.getSimpleName().toString());

            Map<String, Element> propertyAndColumns = new LinkedHashMap<>();
            List<String> defaultColumns = new ArrayList<>();
            TypeElement classElement = (TypeElement) entityClassElement;
            do {
                fillPropertyAndColumns(propertyAndColumns, defaultColumns, classElement);
                classElement = (TypeElement) typeUtils.asElement(classElement.getSuperclass());
            } while (classElement != null);

            StringBuilder tablesContent = new StringBuilder();
            tablesContent.append(buildTablesClass(entityClassElement.getSimpleName().toString(), tableName, propertyAndColumns, defaultColumns));
            if (tablesContent.length() > 0) {
                genTablesClass(guessPackage.toString(), className, tablesContent.toString());
            }
        });
        return false;
    }

    private String buildTablesClass(String entityClass, String tableName, Map<String, Element> propertyAndColumns
            , List<String> defaultColumns) {

        String tableDef = tableDefTemplate.replace("@entityClass", entityClass)
                .replace("@tableField", buildName(entityClass))
                .replace("@tableName", tableName);

        StringBuilder queryColumns = new StringBuilder();
        propertyAndColumns.forEach((property, typeMirror) ->
                queryColumns.append(ColumnsTemplate.get(typeMirror, typeUtils)
                        .replace("@property", property)
                ));

        String tableClass = classTemplate.replace("@entityClass", entityClass)
                .replace("@queryColumns", queryColumns);

        return tableDef + tableClass;
    }

    //upperCase, lowerCase, upperCamelCase, lowerCamelCase
    private static String buildName(String name) {
        return camelToUnderline(name).toUpperCase();
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

    private void genTablesClass(String genPackageName, String className, String classContent) {
        String genContent = classTableTemplate.replace("@package", genPackageName)
                .replace("@classesInfo", classContent)
                .replace("@tablesClassName", className);

        try (Writer writer = filer.createSourceFile(genPackageName + "." + className).openWriter()) {
            writer.write(genContent);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
