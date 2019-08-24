/*
 */
package com.ge.apm.edgeserver;

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;

/**
 * @author 212547631
 */
@Configuration
@EnableJpaRepositories(
        basePackages = "com.ge.apm.edgeserver.dataupload.dao",
        entityManagerFactoryRef = "entityManager",
        transactionManagerRef = "transactionManager"
)
public class AppDbConfig {
    //@Autowired
    //private Environment env;
/*     
    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManager() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(edgeDataSource());
        em.setPackagesToScan(new String[] { "com.ge.apm.edgeserver.dataupload.entity" });
        em.setPersistenceUnitName("edgePU");
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
 
        HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.connection.useUnicode", "true");
        properties.put("hibernate.connection.charSet", "UTF-8");
        em.setJpaPropertyMap(properties);
 
        return em;
    }

    @Primary
    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManager().getObject());
        return transactionManager;
    }
*/
    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSource edgeDataSource() {
        return DataSourceBuilder.create().build();
    }

}
