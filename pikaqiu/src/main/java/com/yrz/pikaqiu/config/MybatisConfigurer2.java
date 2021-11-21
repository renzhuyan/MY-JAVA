package com.yrz.pikaqiu.config;

import com.yrz.pikaqiu.pool.PikaqiuXADataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 配置扫描com.yrz.pikaqiu.mapper.ds2目录下的mapper将连接数据源2操作
 * 配置sqlSessionFactoryRef为sqlSessionTemplate2
 */
@Configuration
@MapperScan(basePackages = {"com.yrz.pikaqiu.mapper.ds2"},sqlSessionTemplateRef = "sqlSessionTemplate2")
public class MybatisConfigurer2 {

    /**
     * jdbc.ds1获取配置信息，初始化pikaqiuDataSource2
     * @return
     */
    @Bean(name="pikaqiuDataSource2")
    @ConfigurationProperties(prefix = "jdbc.ds2")
    public PikaqiuXADataSource dataSource0(){
        PikaqiuXADataSource dataSource = new PikaqiuXADataSource();
        return dataSource;
    }

    /**
     * 实例化AtomikosDataSourceBean，并且set Pikaqiu初始化的PikaqiuXADataSource
     * @param pikaqiuDataSource2
     * @return
     */
    @Bean(name = "dataSource2")
    public AtomikosDataSourceBean dataSource(@Qualifier("pikaqiuDataSource2") PikaqiuXADataSource pikaqiuDataSource2){
        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();

        try {
            xaDataSource.setXaDataSource(pikaqiuDataSource2);
            xaDataSource.setMaxPoolSize(10);
            xaDataSource.setMinPoolSize(5);
            xaDataSource.setUniqueResourceName("dataSource2");
        } catch (Exception e) {
            System.out.println("dataSource2 init error!"+e);
        }
        return xaDataSource;
    }


    /**
     * 将AtomikosDataSourceBean交给SqlSessionFactory
     * @param dataSource
     * @return
     * @throws Exception
     */
    @Bean(name = "sqlSessionFactory2")
    public SqlSessionFactory sqlSessionFactory2(@Qualifier("dataSource2") DataSource dataSource)
            throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        return bean.getObject();
    }

    /**
     * 将sqlSessionFactory交给SqlSessionTemplate管理
     * @param sqlSessionFactory
     * @return
     */
    @Bean(name = "sqlSessionTemplate2")
    public SqlSessionTemplate sqlSessionTemplate2(
            @Qualifier("sqlSessionFactory2") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
