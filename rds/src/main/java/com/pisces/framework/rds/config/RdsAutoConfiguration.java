package com.pisces.framework.rds.config;

import com.pisces.framework.core.config.BaseConfiguration;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * rds汽车配置
 *
 * @author jason
 * @date 2022/12/07
 */
@Configuration(proxyBeanMethods = false)
//@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})
//@ConditionalOnSingleCandidate(DataSource.class)
@EnableConfigurationProperties(RdsProperties.class)
@PropertySource(ignoreResourceNotFound = true, value = "classpath:rds.properties")
//@AutoConfigureAfter({DataSourceAutoConfiguration.class, MybatisPlusLanguageDriverAutoConfiguration.class})
public class RdsAutoConfiguration extends BaseConfiguration {
    protected static final Logger logger = LoggerFactory.getLogger(RdsAutoConfiguration.class);

//    protected final RdsProperties properties;
//
//    protected final Interceptor[] interceptors;
//
//    protected final TypeHandler<?>[] typeHandlers;
//
//    protected final LanguageDriver[] languageDrivers;
//
//    protected final ResourceLoader resourceLoader;
//
//    protected final DatabaseIdProvider databaseIdProvider;
//
//
//    public RdsAutoConfiguration(RdsProperties properties, ObjectProvider<Interceptor[]> interceptorsProvider,
//                                ObjectProvider<TypeHandler<?>[]> typeHandlersProvider, ObjectProvider<LanguageDriver[]> languageDriversProvider,
//                                ResourceLoader resourceLoader, ObjectProvider<DatabaseIdProvider> databaseIdProvider) {
//        this.properties = properties;
//        this.interceptors = interceptorsProvider.getIfAvailable();
//        this.typeHandlers = typeHandlersProvider.getIfAvailable();
//        this.languageDrivers = languageDriversProvider.getIfAvailable();
//        this.resourceLoader = resourceLoader;
//        this.databaseIdProvider = databaseIdProvider.getIfAvailable();
//    }
//
//    @Override
//    public void afterPropertiesSet() {
//        checkConfigFileExists();
//    }
//
//    private void checkConfigFileExists() {
//        if (this.properties.isCheckConfigLocation() && StringUtils.hasText(this.properties.getConfigLocation())) {
//            Resource resource = this.resourceLoader.getResource(this.properties.getConfigLocation());
//            Assert.state(resource.exists(),
//                    "Cannot find config location: " + resource + " (please add config file or check your Mybatis configuration)");
//        }
//    }
//
//    /**
//     * This will just scan the same base package as Spring Boot does. If you want more power, you can explicitly use
//     * {@link org.mybatis.spring.annotation.MapperScan} but this will get typed mappers working correctly, out-of-the-box,
//     * similar to using Spring Data JPA repositories.
//     */
//    public static class RdsAutoConfiguredMapperScannerRegistrar implements BeanFactoryAware, EnvironmentAware, ImportBeanDefinitionRegistrar {
//        private BeanFactory beanFactory;
//        private Environment environment;
//
//        @Override
//        public void registerBeanDefinitions(@NotNull AnnotationMetadata importingClassMetadata, @NotNull BeanDefinitionRegistry registry) {
//            if (!AutoConfigurationPackages.has(this.beanFactory)) {
//                logger.debug("Could not determine auto-configuration package, automatic mapper scanning disabled.");
//                return;
//            }
//
//            logger.debug("Searching for mappers annotated with @Mapper");
//
//            List<String> packages = AutoConfigurationPackages.get(this.beanFactory);
//            if (logger.isDebugEnabled()) {
//                packages.forEach(pkg -> logger.debug("Using auto-configuration base package '{}'", pkg));
//            }
//
//            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MapperScannerConfigurer.class);
//            builder.addPropertyValue("processPropertyPlaceHolders", true);
//            builder.addPropertyValue("annotationClass", Mapper.class);
//            builder.addPropertyValue("basePackage", StringUtils.collectionToCommaDelimitedString(packages));
//            BeanWrapper beanWrapper = new BeanWrapperImpl(MapperScannerConfigurer.class);
//            Set<String> propertyNames = Stream.of(beanWrapper.getPropertyDescriptors()).map(PropertyDescriptor::getName)
//                    .collect(Collectors.toSet());
//            if (propertyNames.contains("lazyInitialization")) {
//                // Need to mybatis-spring 2.0.2+
//                builder.addPropertyValue("lazyInitialization", "${mybatis.lazy-initialization:false}");
//            }
//            if (propertyNames.contains("defaultScope")) {
//                // Need to mybatis-spring 2.0.6+
//                builder.addPropertyValue("defaultScope", "${mybatis.mapper-default-scope:}");
//            }
//
//            // for spring-native
//            boolean injectSqlSession = environment.getProperty("mybatis.inject-sql-session-on-mapper-scan", Boolean.class,
//                    Boolean.TRUE);
//            if (injectSqlSession && this.beanFactory instanceof ListableBeanFactory listableBeanFactory) {
//                Optional<String> sqlSessionTemplateBeanName = Optional
//                        .ofNullable(getBeanNameForType(SqlSessionTemplate.class, listableBeanFactory));
//                Optional<String> sqlSessionFactoryBeanName = Optional
//                        .ofNullable(getBeanNameForType(SqlSessionFactory.class, listableBeanFactory));
//                if (sqlSessionTemplateBeanName.isPresent() || sqlSessionFactoryBeanName.isEmpty()) {
//                    builder.addPropertyValue("sqlSessionTemplateBeanName",
//                            sqlSessionTemplateBeanName.orElse("sqlSessionTemplate"));
//                } else {
//                    builder.addPropertyValue("sqlSessionFactoryBeanName", sqlSessionFactoryBeanName.get());
//                }
//            }
//            builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
//
//            registry.registerBeanDefinition(MapperScannerConfigurer.class.getName(), builder.getBeanDefinition());
//        }
//
//        @Override
//        public void setBeanFactory(@NotNull BeanFactory beanFactory) {
//            this.beanFactory = beanFactory;
//        }
//
//        @Override
//        public void setEnvironment(@NotNull Environment environment) {
//            this.environment = environment;
//        }
//
//        private String getBeanNameForType(Class<?> type, ListableBeanFactory factory) {
//            String[] beanNames = factory.getBeanNamesForType(type);
//            return beanNames.length > 0 ? beanNames[0] : null;
//        }
//    }
//
//    /**
//     * If mapper registering configuration or mapper scanning configuration not present, this configuration allow to scan
//     * mappers based on the same component-scanning path as Spring Boot itself.
//     */
//    @org.springframework.context.annotation.Configuration(proxyBeanMethods = false)
//    @Import(RdsAutoConfiguredMapperScannerRegistrar.class)
//    @ConditionalOnMissingBean({MapperFactoryBean.class, MapperScannerConfigurer.class})
//    public static class RdsMapperScannerRegistrarNotFoundConfiguration implements InitializingBean {
//
//        @Override
//        public void afterPropertiesSet() {
//            logger.debug("Not found configuration for registering mapper bean using @MapperScan, MapperFactoryBean and MapperScannerConfigurer.");
//        }
//
//    }
}