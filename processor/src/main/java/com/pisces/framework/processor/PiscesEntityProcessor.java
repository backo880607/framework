package com.pisces.framework.processor;

import com.google.auto.service.AutoService;
import jakarta.persistence.Table;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Set;
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
            String entityClassName = entityClassElement.toString();
            int num = 0;
        });
        return false;
    }
}
