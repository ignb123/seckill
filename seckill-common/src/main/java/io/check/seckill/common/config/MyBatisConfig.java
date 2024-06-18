package io.check.seckill.common.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * @author check
 * @version 1.0.0
 * @description MyBatis配置类
 */
public class MyBatisConfig {

    @Value("${mybatis.scanpackages}")
    private String scanPackages;

    @Bean
    public SqlSessionFactoryBean sqlSessionFactory(DruidDataSource dataSource){
        SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
        sqlSessionFactory.setTypeAliasesPackage(scanPackages);
        return sqlSessionFactory;
    }
}
