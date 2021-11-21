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
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * 配置扫描com.yrz.pikaqiu.mapper.ds1目录下的mapper将连接数据源1操作
 * 配置sqlSessionFactoryRef为sqlSessionTemplate1
 */
@Configuration
@MapperScan(basePackages = {"com.yrz.pikaqiu.mapper.ds1"},sqlSessionTemplateRef = "sqlSessionTemplate1")
public class MybatisConfigurer1 {

    /**
     * jdbc.ds1获取配置信息，初始化pikaqiuDataSource1
     * @return
     */
    @Bean(name="pikaqiuDataSource1")
    @ConfigurationProperties(prefix = "jdbc.ds1")
    public PikaqiuXADataSource dataSource0(){
        PikaqiuXADataSource dataSource = new PikaqiuXADataSource();
        return dataSource;
    }

    /**
     * 实例化AtomikosDataSourceBean，并且set Pikaqiu初始化的PikaqiuXADataSource
     * @param pikaqiuDataSource1
     * @return
     */
    @Primary
    @Bean(name = "dataSource1")
    public AtomikosDataSourceBean dataSource(@Qualifier("pikaqiuDataSource1") PikaqiuXADataSource pikaqiuDataSource1){
        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();

        try {
            xaDataSource.setXaDataSource(pikaqiuDataSource1);
            xaDataSource.setMaxPoolSize(5);
            xaDataSource.setMinPoolSize(5);
            xaDataSource.setUniqueResourceName("dataSource1");
        } catch (Exception e) {
            System.out.println("dataSource1 init error!"+e);
        }
        return xaDataSource;
    }


    /**
     * 将AtomikosDataSourceBean交给SqlSessionFactory
     * @param dataSource
     * @return
     * @throws Exception
     */
    @Bean(name = "sqlSessionFactory1")
    public SqlSessionFactory sqlSessionFactory1(@Qualifier("dataSource1") DataSource dataSource)
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
    @Bean(name = "sqlSessionTemplate1")
    public SqlSessionTemplate sqlSessionTemplate1(
            @Qualifier("sqlSessionFactory1") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
