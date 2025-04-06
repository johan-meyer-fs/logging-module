package io.skylab.logging_module.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "logging")
public class LoggingProperties
{
	public static class LogstashProperties
	{

	}
}