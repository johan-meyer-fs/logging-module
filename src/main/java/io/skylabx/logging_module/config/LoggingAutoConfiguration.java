package io.skylabx.logging_module.config;

import lombok.extern.slf4j.Slf4j;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
// @EnableConfigurationProperties(LoggingProperties.class)
// @ConditionalOnProperty(name = "logging", havingValue = "true", matchIfMissing = true)
public class LoggingAutoConfiguration
{
	// This class primarily exists to register the properties and enable
	// auto-configuration
	// The actual logging configuration happens in logback-spring.xml
	// private final LoggingProperties properties;
	// public LoggingAutoConfiguration(LoggingProperties properties)
	// {
	// 	System.out.println("Running LoggingAutoConfiguration constructor");
	// 	this.properties = properties;
	// }

	@Bean
    public Logger logger() {
		System.out.println("Creating logger");
        return (Logger) LoggerFactory.getLogger("CustomLogger");
    }
}