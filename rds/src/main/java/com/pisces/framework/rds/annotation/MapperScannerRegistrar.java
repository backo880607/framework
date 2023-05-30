package com.pisces.framework.rds.annotation;

import com.pisces.framework.rds.config.ClassPathMapperScanner;
import com.pisces.framework.rds.config.MapperFactoryBean;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class MapperScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {
    public static final Logger LOGGER = LoggerFactory.getLogger(MapperScannerRegistrar.class);
    private ResourceLoader resourceLoader;
    private Environment environment;

    public MapperScannerRegistrar() {
    }

    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, @NotNull BeanDefinitionRegistry registry) {
        AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(MapperScan.class.getName()));
        ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);
        // this check is needed in Spring 3.1
        if (resourceLoader != null) {
            scanner.setResourceLoader(resourceLoader);
        }

        Class<? extends Annotation> annotationClass = annoAttrs.getClass("annotationClass");
        if (!Annotation.class.equals(annotationClass)) {
            scanner.setAnnotationClass(annotationClass);
        }

        Class<?> markerInterface = annoAttrs.getClass("markerInterface");
        if (!Class.class.equals(markerInterface)) {
            scanner.setMarkerInterface(markerInterface);
        }

        Class<? extends BeanNameGenerator> generatorClass = annoAttrs.getClass("nameGenerator");
        if (!BeanNameGenerator.class.equals(generatorClass)) {
            scanner.setBeanNameGenerator(BeanUtils.instantiateClass(generatorClass));
        }

        Class<? extends MapperFactoryBean<?>> mapperFactoryBeanClass = annoAttrs.getClass("factoryBean");
        if (!MapperFactoryBean.class.equals(mapperFactoryBeanClass)) {
            scanner.setMapperFactoryBean(BeanUtils.instantiateClass(mapperFactoryBeanClass));
        }

        scanner.setSqlSessionTemplateBeanName(annoAttrs.getString("sqlSessionTemplateRef"));
        scanner.setSqlSessionFactoryBeanName(annoAttrs.getString("sqlSessionFactoryRef"));

        List<String> basePackages = new ArrayList<>();
        for (String pkg : annoAttrs.getStringArray("value")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (String pkg : annoAttrs.getStringArray("basePackages")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (Class<?> clazz : annoAttrs.getClassArray("basePackageClasses")) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }
        //优先级 mapperHelperRef > properties > springboot
        String mapperHelperRef = annoAttrs.getString("mapperHelperRef");
        String[] properties = annoAttrs.getStringArray("properties");
        if (StringUtils.hasText(mapperHelperRef)) {
            scanner.setMapperHelperBeanName(mapperHelperRef);
        } else if (properties.length > 0) {
            scanner.setMapperProperties(properties);
        } else {
            try {
                scanner.setMapperProperties(this.environment);
            } catch (Exception e) {
                LOGGER.warn("只有 Spring Boot 环境中可以通过 Environment(配置文件,环境变量,运行参数等方式) 配置通用 Mapper，" +
                        "其他环境请通过 @MapperScan 注解中的 mapperHelperRef 或 properties 参数进行配置!" +
                        "如果你使用 tk.mybatis.mapper.session.Configuration 配置的通用 Mapper，你可以忽略该错误!", e);
            }
        }

        String lazyInitialization = annoAttrs.getString("lazyInitialization");
        if (StringUtils.hasText(lazyInitialization)) {
            scanner.setLazyInitialization(Boolean.parseBoolean(lazyInitialization));
        }

        scanner.registerFilters();
        scanner.doScan(StringUtils.toStringArray(basePackages));
    }

    public void setEnvironment(@NotNull Environment environment) {
        this.environment = environment;
    }

    public void setResourceLoader(@NotNull ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
