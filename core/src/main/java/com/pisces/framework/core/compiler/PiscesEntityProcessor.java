package com.pisces.framework.core.compiler;

import com.google.auto.service.AutoService;
//import com.sun.tools.javac.api.JavacTrees;
//import com.sun.tools.javac.processing.JavacProcessingEnvironment;
//import com.sun.tools.javac.tree.TreeMaker;
//import com.sun.tools.javac.util.Context;
//import com.sun.tools.javac.util.Names;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 该注解表明当前注解处理器仅能处理
 * com.pisces.framework.core.compiler.PiscesEntity注解
 */
//@SupportedAnnotationTypes("com.pisces.framework.core.complier.PiscesEntity")
/**
 * javac调用注解处理器时是使用spi机制调用
 * 因此需要在META-INF下创建spi文件
 * 使用该@AutoService注解可以自动创建
 * Google的工具
 */
//@AutoService(Processor.class)
public class PiscesEntityProcessor extends AbstractProcessor {

    private Elements elementUtils;
//    private JavacTrees javacTrees;
//    private TreeMaker treeMaker;
//    private Names names;

    /**
     * 初始化，此处可以获取各种官方提供的工具类
     *
     * @param processingEnv 处理env
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.elementUtils = processingEnv.getElementUtils();
//
//        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
//        //JCTree工具类
//        this.javacTrees = JavacTrees.instance(processingEnv);
//        //JCTree工具类
//        this.treeMaker = TreeMaker.instance(context);
//        //命名工具类
//        this.names = Names.instance(context);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return false;
        }
        roundEnv.getElementsAnnotatedWith(PiscesEntity.class).forEach((Consumer<Element>) entityClassElement -> {
            String entityClassName = entityClassElement.toString();
            int num = 0;
        });
        return false;
    }
}
