package com.epam.lab.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = "com.epam.lab")
@Import(value = DaoConfig.class)
public class ServiceConfig {
}
