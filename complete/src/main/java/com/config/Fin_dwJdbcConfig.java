package com.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.druid.pool.DruidDataSource;

@Configuration
public class Fin_dwJdbcConfig {
	
	Logger logger = LoggerFactory.getLogger(Fin_dwJdbcConfig.class);
	
	@Value("${spring.datasource.fin_dw.url}")
    private String dbUrl;

    @Value("${spring.datasource.fin_dw.username}")
    private String username;

    @Value("${spring.datasource.fin_dw.password}")
    private String password;

    @Value("${spring.datasource.fin_dw.driverClassName}")
    private String driverClassName;

    @Value("${spring.datasource.fin_dw.initialSize}")
    private int initialSize;

    @Value("${spring.datasource.fin_dw.minIdle}")
    private int minIdle;

    @Value("${spring.datasource.fin_dw.maxActive}")
    private int maxActive;

    @Value("${spring.datasource.fin_dw.maxWait}")
    private int maxWait;

    @Value("${spring.datasource.fin_dw.timeBetweenEvictionRunsMillis}")
    private int timeBetweenEvictionRunsMillis;

    @Value("${spring.datasource.fin_dw.minEvictableIdleTimeMillis}")
    private int minEvictableIdleTimeMillis;

    @Value("${spring.datasource.fin_dw.testWhileIdle}")
    private boolean testWhileIdle;

    @Value("${spring.datasource.fin_dw.testOnBorrow}")
    private boolean testOnBorrow;

    @Value("${spring.datasource.fin_dw.testOnReturn}")
    private boolean testOnReturn;

    @Bean("fin_dw")
    @Qualifier("fin_dw")
    public DruidDataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        logger.info("本次加载impala查询地址为："+dbUrl);
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);
        dataSource.setInitialSize(initialSize);
        dataSource.setMinIdle(minIdle);
        dataSource.setMaxActive(maxActive);
        dataSource.setMaxWait(maxWait);
        dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        dataSource.setTestWhileIdle(testWhileIdle);
        dataSource.setTestOnBorrow(testOnBorrow);
        dataSource.setTestOnReturn(testOnReturn);
        return dataSource;
    }

    @Bean("fin_dwJdbcTemplate")
    public JdbcTemplate jdbcTemplate(@Qualifier("fin_dw") DruidDataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
