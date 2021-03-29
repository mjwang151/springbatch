package com.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * batch数据源配置
 * 对应application-*.yml 下的 spring.datasource.batch
 * @author lfu1
 */
@Configuration
public class BatchJdbcConfig {
	@Value("${spring.datasource.batch.url}")
    private String dbUrl;

    @Value("${spring.datasource.batch.username}")
    private String username;

    @Value("${spring.datasource.batch.password}")
    private String password;

    @Value("${spring.datasource.batch.driverClassName}")
    private String driverClassName;

    @Value("${spring.datasource.batch.initialSize}")
    private int initialSize;

    @Value("${spring.datasource.batch.minIdle}")
    private int minIdle;

    @Value("${spring.datasource.batch.maxActive}")
    private int maxActive;

    @Value("${spring.datasource.batch.maxWait}")
    private int maxWait;

    @Value("${spring.datasource.batch.timeBetweenEvictionRunsMillis}")
    private int timeBetweenEvictionRunsMillis;

    @Value("${spring.datasource.batch.minEvictableIdleTimeMillis}")
    private int minEvictableIdleTimeMillis;

    @Value("${spring.datasource.batch.testWhileIdle}")
    private boolean batchWhileIdle;

    @Value("${spring.datasource.batch.testOnBorrow}")
    private boolean batchOnBorrow;

    @Value("${spring.datasource.batch.testOnReturn}")
    private boolean batchOnReturn;
    
    @Bean("batch")
    @Qualifier("batch")
    @Primary
    public DataSource dataSource() {
    	DruidDataSource dataSource = new DruidDataSource();
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
        dataSource.setValidationQuery("select 1");
        dataSource.setTestWhileIdle(batchWhileIdle);
        dataSource.setTestOnBorrow(batchOnBorrow);
        dataSource.setTestOnReturn(batchOnReturn);
        return dataSource;
    }
    
    @Bean("batchTxManager")
    @Primary
    public PlatformTransactionManager txManager(@Qualifier("batch") DruidDataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
    
    @Bean("batchJdbcTemplate")
    @Primary
    public JdbcTemplate jdbcTemplate(@Qualifier("batch") DruidDataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
