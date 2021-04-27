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
 * batchOracle数据源配置
 * 对应application-*.yml 下的 spring.datasource.batchOracle
 * @author lfu1
 */
@Configuration
public class BatchOracleJdbcConfig {
	@Value("${spring.datasource.batchOracle.url}")
    private String dbUrl;

    @Value("${spring.datasource.batchOracle.username}")
    private String username;

    @Value("${spring.datasource.batchOracle.password}")
    private String password;

    @Value("${spring.datasource.batchOracle.driverClassName}")
    private String driverClassName;

    @Value("${spring.datasource.batchOracle.initialSize}")
    private int initialSize;

    @Value("${spring.datasource.batchOracle.minIdle}")
    private int minIdle;

    @Value("${spring.datasource.batchOracle.maxActive}")
    private int maxActive;

    @Value("${spring.datasource.batchOracle.maxWait}")
    private int maxWait;

    @Value("${spring.datasource.batchOracle.timeBetweenEvictionRunsMillis}")
    private int timeBetweenEvictionRunsMillis;

    @Value("${spring.datasource.batchOracle.minEvictableIdleTimeMillis}")
    private int minEvictableIdleTimeMillis;

    @Value("${spring.datasource.batchOracle.testWhileIdle}")
    private boolean batchOracleWhileIdle;

    @Value("${spring.datasource.batchOracle.testOnBorrow}")
    private boolean batchOracleOnBorrow;

    @Value("${spring.datasource.batchOracle.testOnReturn}")
    private boolean batchOracleOnReturn;
    
    @Bean("batchOracle")
    @Qualifier("batchOracle")
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
        dataSource.setTestWhileIdle(batchOracleWhileIdle);
        dataSource.setTestOnBorrow(batchOracleOnBorrow);
        dataSource.setTestOnReturn(batchOracleOnReturn);
        return dataSource;
    }


    @Bean("batchOracleTxManager")
    public PlatformTransactionManager txManager(@Qualifier("batchOracle") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
    
    @Bean("batchOracleJdbcTemplate")
    public JdbcTemplate jdbcTemplate(@Qualifier("batchOracle") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
