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
import com.pisces.framework.rds.datasource.creator.DataSourceCreator;
import com.pisces.framework.rds.datasource.creator.DefaultDataSourceCreator;
import com.pisces.framework.rds.datasource.provider.DynamicDataSourceProvider;
import com.pisces.framework.rds.datasource.provider.YmlDynamicDataSourceProvider;
import com.pisces.framework.rds.datasource.strategy.DynamicDataSourceStrategy;
import com.pisces.framework.rds.datasource.event.DataSourceInitEvent;
import com.pisces.framework.rds.datasource.event.EncDataSourceInitEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;

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
@RequiredArgsConstructor
@EnableConfigurationProperties(RdsProperties.class)
public class DynamicDataSourceAssistConfiguration {

    private final RdsProperties properties;

    @Bean
    @Order(0)
    public DynamicDataSourceProvider ymlDynamicDataSourceProvider(DefaultDataSourceCreator defaultDataSourceCreator) {
        return new YmlDynamicDataSourceProvider(defaultDataSourceCreator, properties.getDatasource());
    }

    @Bean
    @ConditionalOnMissingBean
    public DataSourceInitEvent dataSourceInitEvent() {
        return new EncDataSourceInitEvent();
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultDataSourceCreator dataSourceCreator(List<DataSourceCreator> dataSourceCreators) {
        DefaultDataSourceCreator creator = new DefaultDataSourceCreator();
        creator.setCreators(dataSourceCreators);
        creator.setPublicKey(properties.getPublicKey());
        creator.setLazy(properties.getLazy());
        creator.setP6spy(properties.getP6spy());
        creator.setSeata(properties.getSeata());
        creator.setSeataMode(properties.getSeataMode());
        return creator;
    }
}