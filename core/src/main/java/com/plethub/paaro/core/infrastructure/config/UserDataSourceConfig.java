package com.plethub.paaro.core.infrastructure.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableJpaRepositories(basePackages = {"com.plethub.paaro.core.*"}, entityManagerFactoryRef = "localContainerEntityManagerFactoryBean" ,transactionManagerRef = "platformTransactionManager" )
public class UserDataSourceConfig {

    @Autowired
    private Environment environment;

    @Bean
    public LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean() {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(primaryDataSource());
        em.setPackagesToScan(
                new String[] { "com.plethub.paaro.core" });

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto",
                environment.getProperty("hibernate.hbm2ddl.auto"));
        properties.put("hibernate.dialect",
                environment.getProperty("hibernate.dialect"));
        em.setJpaPropertyMap(properties);
        return em;
    }

    @Bean
    public DataSource primaryDataSource() {

        DriverManagerDataSource dataSource
                = new DriverManagerDataSource();
        dataSource.setDriverClassName(
                environment.getProperty("usersource.jdbc.driverClassName"));
        dataSource.setUrl(environment.getProperty("usersource.jdbc.url"));
        dataSource.setUsername(environment.getProperty("usersource.jdbc.user"));
        dataSource.setPassword(environment.getProperty("usersource.jdbc.password"));
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager platformTransactionManager() {

        JpaTransactionManager transactionManager
                = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(
                localContainerEntityManagerFactoryBean().getObject());
        return transactionManager;
    }


}
