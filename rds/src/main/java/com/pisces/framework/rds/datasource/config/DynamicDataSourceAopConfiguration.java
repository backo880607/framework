/*
 * Copyright © 2018 organization baomidou
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pisces.framework.rds.datasource.config;

import com.pisces.framework.rds.config.RdsProperties;
import com.pisces.framework.rds.datasource.DynamicRoutingDataSource;
import com.pisces.framework.rds.datasource.processor.DsHeaderProcessor;
import com.pisces.framework.rds.datasource.processor.DsProcessor;
import com.pisces.framework.rds.datasource.processor.DsSessionProcessor;
import com.pisces.framework.rds.datasource.processor.DsSpelExpressionProcessor;
import com.pisces.framework.rds.datasource.provider.DynamicDataSourceProvider;
import com.pisces.framework.rds.datasource.strategy.DynamicDataSourceStrategy;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.expression.BeanFactoryResolver;

/**
 * 动态数据源核心自动配置类
 *
 * @author TaoYu Kanyuxia
 * @see DynamicDataSourceProvider
 * @see DynamicDataSourceStrategy
 * @see DynamicRoutingDataSource
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties(RdsProperties.class)
public class DynamicDataSourceAopConfiguration {

    private final RdsProperties properties;

    public DynamicDataSourceAopConfiguration(RdsProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean
    public DsProcessor dsProcessor(BeanFactory beanFactory) {
        DsProcessor headerProcessor = new DsHeaderProcessor();
        DsProcessor sessionProcessor = new DsSessionProcessor();
        DsSpelExpressionProcessor spelExpressionProcessor = new DsSpelExpressionProcessor();
        spelExpressionProcessor.setBeanResolver(new BeanFactoryResolver(beanFactory));
        headerProcessor.setNextProcessor(sessionProcessor);
        sessionProcessor.setNextProcessor(spelExpressionProcessor);
        return headerProcessor;
    }


//    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
//    @Bean
//    @ConditionalOnProperty(prefix = DynamicDataSourceProperties.PREFIX + ".aop", name = "enabled", havingValue = "true", matchIfMissing = true)
//    public Advisor dynamicDatasourceAnnotationAdvisor(DsProcessor dsProcessor) {
//        DynamicDatasourceAopProperties aopProperties = properties.getAop();
//        DynamicDataSourceAnnotationInterceptor interceptor = new DynamicDataSourceAnnotationInterceptor(aopProperties.getAllowedPublicOnly(), dsProcessor);
//        DynamicDataSourceAnnotationAdvisor advisor = new DynamicDataSourceAnnotationAdvisor(interceptor, DS.class);
//        advisor.setOrder(aopProperties.getOrder());
//        return advisor;
//    }

//    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
//    @Bean
//    @ConditionalOnProperty(prefix = DynamicDataSourceProperties.PREFIX, name = "seata", havingValue = "false", matchIfMissing = true)
//    public Advisor dynamicTransactionAdvisor() {
//        DynamicLocalTransactionInterceptor interceptor = new DynamicLocalTransactionInterceptor();
//        return new DynamicDataSourceAnnotationAdvisor(interceptor, DSTransactional.class);
//    }

}