package com.pisces.framework.processor;

import com.pisces.framework.type.Duration;
import com.pisces.framework.type.PROPERTY_TYPE;
import com.pisces.framework.type.annotation.PropertyMeta;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.Date;

/**
 * 列模板
 *
 * @author jason
 * @date 2023/06/25
 */
public class ColumnsTemplate {
    private static final String BOOLEAN_TEMPLATE = "        public BooleanQueryColumn @property = new BooleanQueryColumn(this, \"@property\");\n";
    private static final String CHAR_TEMPLATE = "        public CharQueryColumn @property = new CharQueryColumn(this, \"@property\");\n";
    private static final String SHORT_TEMPLATE = "        public ShortQueryColumn @property = new ShortQueryColumn(this, \"@property\");\n";
    private static final String INTEGER_TEMPLATE = "        public IntegerQueryColumn @property = new IntegerQueryColumn(this, \"@property\");\n";
    private static final String LONG_TEMPLATE = "        public LongQueryColumn @property = new LongQueryColumn(this, \"@property\");\n";
    private static final String STRING_TEMPLATE = "        public StringQueryColumn @property = new StringQueryColumn(this, \"@property\");\n";
    private static final String DOUBLE_TEMPLATE = "        public DoubleQueryColumn @property = new DoubleQueryColumn(this, \"@property\");\n";
    private static final String DATE_TEMPLATE = "        public DateQueryColumn @property = new DateQueryColumn(this, \"@property\");\n";
    private static final String TIME_TEMPLATE = "        public TimeQueryColumn @property = new TimeQueryColumn(this, \"@property\");\n";
    private static final String DATE_TIME_TEMPLATE = "        public DateTimeQueryColumn @property = new DateTimeQueryColumn(this, \"@property\");\n";
    private static final String DURATION_TEMPLATE = "        public DurationQueryColumn @property = new DurationQueryColumn(this, \"@property\");\n";
    private static final String ENUM_TEMPLATE = "        public EnumQueryColumn @property = new EnumQueryColumn(this, \"@property\");\n";
    private static final String MULTI_ENUM_TEMPLATE = "        public MultiEnumQueryColumn @property = new MultiEnumQueryColumn(this, \"@property\");\n";
    private static final String BEAN_TEMPLATE = "        public BeanQueryColumn @property = new BeanQueryColumn(this, \"@property\");\n";
    private static final String LIST_TEMPLATE = "        public ListQueryColumn @property = new ListQueryColumn(this, \"@property\");\n";

    public static String get(Element element, Types typeUtils) {
        String template = "";
        switch (getPropertyType(element, typeUtils)) {
            case BOOLEAN -> template = BOOLEAN_TEMPLATE;
            case CHAR -> template = CHAR_TEMPLATE;
            case SHORT -> template = SHORT_TEMPLATE;
            case INTEGER -> template = INTEGER_TEMPLATE;
            case LONG -> template = LONG_TEMPLATE;
            case DOUBLE -> template = DOUBLE_TEMPLATE;
            case DATE -> template = DATE_TEMPLATE;
            case TIME -> template = TIME_TEMPLATE;
            case DATE_TIME -> template = DATE_TIME_TEMPLATE;
            case DURATION -> template = DURATION_TEMPLATE;
            case ENUM -> template = ENUM_TEMPLATE;
            case MULTI_ENUM -> template = MULTI_ENUM_TEMPLATE;
            case STRING -> template = STRING_TEMPLATE;
            case BEAN -> template = BEAN_TEMPLATE;
            case LIST -> template = LIST_TEMPLATE;
        }
        return template;
    }

    private static PROPERTY_TYPE getPropertyType(Element fieldElement, Types typeUtils) {
        PROPERTY_TYPE type = PROPERTY_TYPE.NONE;
        PropertyMeta meta = fieldElement.getAnnotation(PropertyMeta.class);
        if (meta != null) {
            if (!meta.property()) {
                return type;
            }
            if (meta.type() != null && meta.type() != PROPERTY_TYPE.NONE) {
                return meta.type();
            }
        }
        TypeMirror typeMirror = fieldElement.asType();
        Element element = typeUtils.asElement(typeMirror);
        if (element != null) {
            typeMirror = element.asType();
        }

        final String typeName = typeMirror.toString();
        if (typeName.equals(Boolean.class.getName())) {
            type = PROPERTY_TYPE.BOOLEAN;
        } else if (typeName.equals(Character.class.getName())) {
            type = PROPERTY_TYPE.CHAR;
        } else if (typeName.equals(Short.class.getName())) {
            type = PROPERTY_TYPE.SHORT;
        } else if (typeName.equals(Integer.class.getName())) {
            type = PROPERTY_TYPE.INTEGER;
        } else if (typeName.equals(Long.class.getName())) {
            type = PROPERTY_TYPE.LONG;
        } else if (typeName.equals(Double.class.getName()) || typeName.equals(Float.class.getName())) {
            type = PROPERTY_TYPE.DOUBLE;
        } else if (typeName.equals(Date.class.getName()) || typeName.equals(java.sql.Date.class.getName()) || "java.sql.Timestamp".equals(typeName)) {
            type = PROPERTY_TYPE.DATE_TIME;
        } else if (typeName.equals(Duration.class.getName())) {
            type = PROPERTY_TYPE.DURATION;
        } else if (typeName.equals(String.class.getName())) {
            type = PROPERTY_TYPE.STRING;
        } else if (typeMirror.getKind() == TypeKind.DECLARED) {
            TypeElement typeElement = (TypeElement) ((DeclaredType) typeMirror).asElement();
            if (typeElement.getKind() == ElementKind.ENUM) {
                type = PROPERTY_TYPE.ENUM;
            } else {
                int num = 0;
            }
        } else {
            int num = 0;
        }
        return type;
    }
}
