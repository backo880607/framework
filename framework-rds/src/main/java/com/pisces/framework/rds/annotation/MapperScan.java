package com.pisces.framework.rds.annotation;

import com.pisces.framework.rds.config.MapperFactoryBean;
import com.pisces.framework.rds.config.MapperScannerRegistrar;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 映射器扫描
 *
 * @author jason
 * @date 2023/06/27
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({MapperScannerRegistrar.class})
public @interface MapperScan {
    String[] basePackages() default {};

    Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;

    Class<? extends Annotation> annotationClass() default Annotation.class;

    String sqlSessionTemplateRef() default "";

    String sqlSessionFactoryRef() default "";

    Class<? extends MapperFactoryBean> factoryBean() default MapperFactoryBean.class;
}
