package com.pisces.framework.rds.config;

import com.pisces.framework.core.config.BaseProperties;
import com.pisces.framework.rds.datasource.DynamicDatasourceAopProperties;
import com.pisces.framework.rds.datasource.creator.DataSourceProperty;
import com.pisces.framework.rds.datasource.creator.hikaricp.HikariCpConfig;
import com.pisces.framework.rds.datasource.strategy.DynamicDataSourceStrategy;
import com.pisces.framework.rds.datasource.strategy.LoadBalanceDynamicDataSourceStrategy;
import com.pisces.framework.rds.enums.SeataMode;
import com.pisces.framework.rds.utils.CryptoUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * rds属性
 *
 * @author jason
 * @date 2022/12/07
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "pisces.framework.rds")
public class RdsProperties extends BaseProperties {
    public RdsProperties() {
        super(RdsConstant.IDENTIFY);
    }

    /**
     * 必须设置默认的库,默认master
     */
    private String primary = "master";
    /**
     * 是否启用严格模式,默认不启动. 严格模式下未匹配到数据源直接报错, 非严格模式下则使用默认数据源primary所设置的数据源
     */
    private Boolean strict = false;
    /**
     * 是否使用p6spy输出，默认不输出
     */
    private Boolean p6spy = false;
    /**
     * 是否使用开启seata，默认不开启
     */
    private Boolean seata = false;
    /**
     * 是否懒加载数据源
     */
    private Boolean lazy = false;
    /**
     * seata使用模式，默认AT
     */
    private SeataMode seataMode = SeataMode.AT;
    /**
     * 全局默认publicKey
     */
    private String publicKey = CryptoUtils.DEFAULT_PUBLIC_KEY_STRING;
    /**
     * 每一个数据源
     */
    private Map<String, DataSourceProperty> datasource = new LinkedHashMap<>();
    /**
     * 多数据源选择算法clazz，默认负载均衡算法
     */
    private Class<? extends DynamicDataSourceStrategy> strategy = LoadBalanceDynamicDataSourceStrategy.class;
    /**
     * HikariCp全局参数配置
     */
    private HikariCpConfig hikari = new HikariCpConfig();

    /**
     * aop with default ds annotation
     */
    private DynamicDatasourceAopProperties aop = new DynamicDatasourceAopProperties();
}
