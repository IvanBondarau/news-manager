package com.epam.lab.configuration;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@ComponentScan(
        basePackages = {"com.epam.lab"}
)
public class DaoConfig {

    @Bean
    public EntityManager getEntityManager(EntityManagerFactory factory) {
        return factory.createEntityManager();
    }

    @Bean
    public EntityManagerFactory getEntityManagerFactory() {
        return Persistence.createEntityManagerFactory("com.epam.lab.dao");
    }

    @Bean
    public PlatformTransactionManager getPlatformTransactionManager(EntityManagerFactory factory) {
        return new JpaTransactionManager(factory);
    }
}
