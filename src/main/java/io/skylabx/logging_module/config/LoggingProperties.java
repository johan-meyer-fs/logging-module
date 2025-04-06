package io.skylabx.logging_module.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "logging")
public class LoggingProperties
{
	public static class LogstashProperties
	{

	}
}