package com.pisces.framework.processor;

/**
 * 类表模板
 *
 * @author jason
 * @date 2023/06/27
 */
public class ClassTableTemplate {
    public static final String TEMPLATE = "package @package;\n" +
            "\n" +
            "import com.pisces.framework.core.query.column.*;\n" +
            "\n" +
            "// Auto generate by framework, do not modify it.\n" +
            "public class Q@entityClass {\n" +
            "@queryColumns" +
            "}\n";
}
