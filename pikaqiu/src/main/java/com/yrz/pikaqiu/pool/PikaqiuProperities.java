package com.yrz.pikaqiu.pool;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.datasource.pikaqiu")
public class PikaqiuProperities {

    private Integer initialSize;
    private Integer minIdle;
    private Integer maxActive;
    private Integer maxWait;


}
