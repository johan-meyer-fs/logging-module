package io.skylabx.logging_module.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
// @EnableConfigurationProperties(LoggingProperties.class)
// @ConditionalOnProperty(name = "logging", havingValue = "true", matchIfMissing = true)
public class LoggingAutoConfiguration
{
	// This class primarily exists to register the properties and enable
	// auto-configuration
	// The actual logging configuration happens in logback-spring.xml

	private final VectorConfigGenerator vectorConfigGenerator;
	// private final LoggingProperties properties;

	public LoggingAutoConfiguration(VectorConfigGenerator vectorConfigGenerator)
	{
		System.out.println("Running LoggingAutoConfiguration constructor");
		this.vectorConfigGenerator = vectorConfigGenerator;

	}
}