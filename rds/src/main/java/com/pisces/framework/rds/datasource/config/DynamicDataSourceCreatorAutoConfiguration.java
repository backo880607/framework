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
import com.pisces.framework.rds.datasource.creator.BasicDataSourceCreator;
import com.pisces.framework.rds.datasource.creator.JndiDataSourceCreator;
import com.pisces.framework.rds.datasource.creator.hikaricp.HikariDataSourceCreator;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @author TaoYu
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(RdsProperties.class)
public class DynamicDataSourceCreatorAutoConfiguration {

    public static final int JNDI_ORDER = 1000;
    public static final int HIKARI_ORDER = 3000;
    public static final int DEFAULT_ORDER = 7000;
    private final RdsProperties properties;

    @Bean
    @Order(DEFAULT_ORDER)
    public BasicDataSourceCreator basicDataSourceCreator() {
        return new BasicDataSourceCreator();
    }

    @Bean
    @Order(JNDI_ORDER)
    public JndiDataSourceCreator jndiDataSourceCreator() {
        return new JndiDataSourceCreator();
    }

    /**
     * 存在Hikari数据源时, 加入创建器
     */
    @Bean
    @Order(HIKARI_ORDER)
    @ConditionalOnClass(HikariDataSource.class)
    public HikariDataSourceCreator hikariDataSourceCreator() {
        return new HikariDataSourceCreator(properties.getHikari());
    }

}