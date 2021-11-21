package com.yrz.pikaqiu.pool;


import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({PikaqiuProperities.class, DataSourceProperties.class})
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
public class PoolConfigurer {

    @Bean(initMethod = "init")
    @ConditionalOnMissingBean
    public PikaqiuDataSource dataSource() {
        return new PikaqiuDataSource();
    }
}
