package com.springboot.demo;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import com.springboot.demo.model.BoardDAO;

/**
 * @Memo: 테스트 설정 클래스
 * (@PropertySource 를 사용하여 application.properties 파일을 포함한다.)
 */
@TestConfiguration
@PropertySource("application.properties")
public class DatabaseTestConfig {
	@Value("${spring.datasource.driver-class-name}")
    private String dataSourceDriverClassName;
	
	@Value("${spring.datasource.url}")
    private String dataSourceUrl;
	
	@Value("${spring.datasource.username}")
    private String dataSourceUsername;
	
	@Value("${spring.datasource.password}")
    private String dataSourcePassword;
	
	/**
     * @Memo: UserDAO 빈 등록
     */
    @Bean
    public BoardDAO createBoardDAO() {
        return new BoardDAO();
    }
    
    /**
     * @Memo: DataSource 빈 등록
     * (https://krksap.tistory.com/1276)
     */
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(dataSourceDriverClassName);
        dataSource.setUrl(dataSourceUrl);
        dataSource.setUsername(dataSourceUsername);
        dataSource.setPassword(dataSourcePassword);
        
        return dataSource;
    }
    /**
     * @Memo: SqlSessionFactory 빈 등록
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(ApplicationContext applicationContext, DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
        sqlSessionFactory.setTypeAliasesPackage("com.springboot.demo.model"); // Model 패키지 내의 VO 클래스를 TypeAlias 로 등록
        // sqlSessionFactory.setConfigLocation(applicationContext.getResource("classpath:/mybatis-config.xml")); // Config.xml 이 없으므로 제외
        sqlSessionFactory.setMapperLocations(applicationContext.getResources("classpath:mapper/*.xml"));
        
        return sqlSessionFactory.getObject();
    }
    
    /**
     * @Memo: SqlSessionTemplate 빈 등록
     * (SqlSession 의 구현체(SqlSessionTemplate)를 사용하여 Thread-Safe)
     * (https://www.holaxprogramming.com/2015/10/18/spring-boot-with-mybatis/)
     */
    @Bean(destroyMethod = "clearCache")
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
    /**
     * @Memo: TransactionManager 빈 등록
     * (DataSource 의 트랜잭션을 관리하는 Manager)
     * (https://www.holaxprogramming.com/2015/10/18/spring-boot-with-mybatis/)
     */
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);
        
        return transactionManager;
    }
}
